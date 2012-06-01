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
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.netty.SpdyHttpRequest;
import com.laxser.tentaclex.commons.spdy.datastructure.ExpireWheel;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.Frame;
import com.laxser.tentaclex.commons.spdy.frame.control.RstStream;
import com.laxser.tentaclex.commons.spdy.frame.control.SynReply;
import com.laxser.tentaclex.commons.spdy.frame.control.SynStream;

/**
 * 把Frame对象(串)还原为HttpRequest对象
 * 
 * @author laxser  Date 2012-5-31 下午4:58:03
@contact [duqifan@gmail.com]
@FrameRequestDecoder.java

 * 
 */

@ChannelPipelineCoverage("one")
public class FrameRequestDecoder extends SimpleChannelUpstreamHandler {
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
    private ExpireWheel<List<Frame>> frameBuffer = new ExpireWheel<List<Frame>>(
			ExpireWheel.CAPACITY_2P14, 2);
    
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        //TODO:处理disconnected
        super.channelDisconnected(ctx, e);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof Frame) {
            Frame frame = (Frame) e.getMessage();
            
            if (frame instanceof SynStream) {//这是要开启一个数据流呢
                SynStream ss = (SynStream) frame;
                /*int streamId = ss.getStreamID();
                if (frameBuffer.containsKey(streamId)) {//如果已经有了？出错
                    assert (false);
                    //TODO:出错处理
                }*/
                
                if ((ss.getFlags() & ControlFrame.FLAG_FIN) == ControlFrame.FLAG_FIN) {//已经是最后一帧了
                    HttpRequest request = buildRequest(ss, null);
                    //交由下面处理
                    Channels.fireMessageReceived(ctx, request, e.getRemoteAddress());
                    //TODO:处理半关连接
                } else {//不是最后一帧，缓存住
                    //由于收到的是SynStream,必须为一个新的流，所以开新list
                    List<Frame> frameList = new LinkedList<Frame>();
                    frameList.add(ss);
                    frameBuffer.put(ss.getStreamId(), frameList);
                }
            } else if (frame instanceof RstStream) {
                //TODO:连接关闭

            } else if (frame instanceof DataFrame) {
                DataFrame dataFrame = (DataFrame) frame;
				List<Frame> frameList = frameBuffer
						.get(dataFrame.getStreamId());
				if (frameList == null) {
					logger.warn("no frame-list for DataStream:"
							+ dataFrame.getStreamId());
					//有可能是应该由ResponseDecoder处理的DataFrame
					super.messageReceived(ctx, e);
                    return;
				} else {
					frameList.add(dataFrame);
					if ((dataFrame.getFlags() & ControlFrame.FLAG_FIN) == ControlFrame.FLAG_FIN) { // 最后一帧
						SynStream headerFrame = (SynStream) frameList.remove(0);//必须是头帧
						HttpRequest request = buildRequest(headerFrame,
								(List) frameList);
						frameBuffer.remove(headerFrame.getStreamId());
						Channels.fireMessageReceived(ctx, request, e
								.getRemoteAddress());
						// TODO:处理半关连接
					}
				}
                
                /*if (frameBuffer.get(dataFrame.getStreamId()) == null) {
                    //不属于SynStream的DataFrame
                    super.messageReceived(ctx, e);
                    return;
                } else if ((dataFrame.getFlags() & ControlFrame.FLAG_FIN) == 1) {//最后一帧
                    List<Frame> list = frameBuffer.get(dataFrame.getStreamId());
                    SynStream headerFrame = (SynStream) list.get(0);//必须是头帧
                    List<DataFrame> dataList = new LinkedList<DataFrame>();
                    for (Frame frameElement : list) {
                        if (frameElement instanceof DataFrame) {//应该是必须的，这里只检查一下
                            dataList.add((DataFrame) frameElement);
                        }
                    }
                    dataList.add(dataFrame);
                    HttpRequest request = buildRequest(headerFrame, dataList);
                    //清理
                    frameBuffer.remove(headerFrame.getStreamID());
                    Channels.fireMessageReceived(ctx, request, e.getRemoteAddress());
                    //TODO:处理半关连接
                } else {//不是最后一帧，缓存
                    List<Frame> list = frameBuffer.get(dataFrame.getStreamId());
                    list.add(dataFrame);
                }*/
            } else if (frame instanceof SynReply) {
                //这是一个response，略过
                super.messageReceived(ctx, e);
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    private SpdyHttpRequest buildRequest(SynStream header, List<DataFrame> dataFrames) {
        Map<String, String> httpHeader = header.getNVMap();
        //version
        HttpVersion httpVersion = httpHeader.get("version").trim().equalsIgnoreCase("HTTP/1.1") ? HttpVersion.HTTP_1_1
                : HttpVersion.HTTP_1_0;
        //method
        HttpMethod httpMethod = null;
        String method = httpHeader.get("method");
        if (method.equalsIgnoreCase("get")) {
            httpMethod = HttpMethod.GET;
        } else if (method.equalsIgnoreCase("post")) {
            httpMethod = HttpMethod.POST;
        } else if (method.equalsIgnoreCase("delete")) {
            httpMethod = HttpMethod.DELETE;
        } else if (method.equalsIgnoreCase("put")) {
            httpMethod = HttpMethod.PUT;
        } else {
        	throw new RuntimeException("Unsupported method:" + method);
        }
        //url
        /*
         * SPDY协议中规定的header中传过来的url字段是一个绝对的URL
         * 路径，而之前的把版本中忽略掉了这点，认为传来的是URI相对路径，
         * 这里要做一个判断和兼容。
         * 
         * */
        String url = httpHeader.get("url");
        String uri;
        if (url.startsWith("http://")) {
        	url = url.substring("http://".length());
        	int index = url.indexOf('/');
        	if (index >= 0) {
        		uri = url.substring(index);
        		if (uri.length() == 0) {	//'/'可能是最后一个字符，此时uri为空串
        			uri = "/";
        		}
        	} else {
        		uri = "/";
        	}
        } else {
        	uri = url;
        }
        
        SpdyHttpRequest request = new SpdyHttpRequest(httpVersion, httpMethod, uri, header
                .getStreamId());
        //set header
        for (Entry<String, String> entry : httpHeader.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
        if (dataFrames != null && dataFrames.size() > 0) {//有body
            byte[][] datas = new byte[dataFrames.size()][];
			int offset = 0;
			for (DataFrame dataFrame : dataFrames) {
				datas[offset++] = dataFrame.getData();
			}
			request.setContent(ChannelBuffers.wrappedBuffer(datas));
        }
        return request;
    }
}
