/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.codec.http;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;
import com.laxser.tentaclex.commons.spdy.datastructure.ExpireWheel;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.Frame;
import com.laxser.tentaclex.commons.spdy.frame.control.RstStream;
import com.laxser.tentaclex.commons.spdy.frame.control.SynReply;
import com.laxser.tentaclex.commons.spdy.frame.control.SynStream;

/**
 * 对SPDY的frame进行解码，还原成{@link SpdyHttpResponse}
 * 
 * @author 李伟博, 刘敬思
 * @since 2010-4-12 下午07:09:59
 */
@ChannelPipelineCoverage("one")
public class MultiFrameResponseDecoder extends SimpleChannelUpstreamHandler {

	protected Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 用ExpireWheel来缓存一个stream中的Frame，直到最后一个Frame到达，然后将这些Frame
	 * 拼装成一个完整的response。
	 * 
	 * buffer的大小是2^14（约1.6W），当前客户端没每秒处理1W次请求，那么
	 * 这样大小的buffer能保证一个stream至少要1.6s才过期，大小应该是够用了。
	 * 
	 */
	private ExpireWheel<List<Frame>> frameBuffer = new ExpireWheel<List<Frame>>(
			ExpireWheel.CAPACITY_2P14, 2);
	
	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof Frame) {
			Frame frame = (Frame) e.getMessage();
			if (frame instanceof SynReply) {// 这是要开启一个数据流呢
				SynReply sr = (SynReply) frame;
				if ((sr.getFlags() & ControlFrame.FLAG_FIN) == ControlFrame.FLAG_FIN) {// 已经是最后一帧了
					//System.out.println("single frame for stream:" + sr.getStreamID());
					HttpResponse response = buildResponse(sr, null);
					// 交由下面处理
					Channels.fireMessageReceived(ctx, response, e
							.getRemoteAddress());
					// TODO:处理半关连接
				} else {// 不是最后一帧，缓存住
					// 由于收到的是SynStream,必须为一个新的流，所以开新list
					List<Frame> frameList = new LinkedList<Frame>();
					frameList.add(sr);
					frameBuffer.put(sr.getStreamId(), frameList);
					//System.out.println("add frame-list for stream:" + sr.getStreamID());
				}
			} else if (frame instanceof RstStream) {
				// TODO:连接关闭

			} else if (frame instanceof DataFrame) {
				DataFrame dataFrame = (DataFrame) frame;

				List<Frame> frameList = frameBuffer
						.get(dataFrame.getStreamId());

				if (frameList == null) {
					logger.warn("no frame-list for DataStream:"
							+ dataFrame.getStreamId());
					//有可能是应该由RequestDecoder处理的DataFrame
					super.messageReceived(ctx, e);
                    return;
				} else {
					frameList.add(dataFrame);
					if ((dataFrame.getFlags() & ControlFrame.FLAG_FIN) == ControlFrame.FLAG_FIN) { // 最后一帧
						SynReply headerFrame = (SynReply) frameList.remove(0);// 必须是头帧
						@SuppressWarnings("rawtypes")
						HttpResponse response = buildResponse(headerFrame, (List)frameList);
						frameBuffer.remove(headerFrame.getStreamId());
						Channels.fireMessageReceived(ctx, response, e
								.getRemoteAddress());
						// TODO:处理半关连接
					}
				}
			} else if (frame instanceof SynStream) {
				// 这是一个request，略过
				super.messageReceived(ctx, e);
			}
		} else {
			super.messageReceived(ctx, e);
		}
	}

	private SpdyHttpResponse buildResponse(SynReply header,
			List<DataFrame> dataFrames) {
		Map<String, String> httpHeader = header.getNVMap();
		HttpVersion httpVersion = httpHeader.get("version").trim()
				.equalsIgnoreCase("HTTP/1.1") ? HttpVersion.HTTP_1_1
				: HttpVersion.HTTP_1_0;
		int status = Integer.parseInt(httpHeader.get("status"));
		SpdyHttpResponse response = new SpdyHttpResponse(httpVersion,
				HttpResponseStatus.valueOf(status), header.getStreamId());

		for (Entry<String, String> entry : httpHeader.entrySet()) {
			response.setHeader(entry.getKey(), entry.getValue());
		}

		if (dataFrames != null && dataFrames.size() > 0) {
			
			byte[][] datas = new byte[dataFrames.size()][];
			int offset = 0;
			for (DataFrame dataFrame : dataFrames) {
				datas[offset++] = dataFrame.getData();
			}
			response.setContent(ChannelBuffers.wrappedBuffer(datas));
		}
		return response;
	}
}
