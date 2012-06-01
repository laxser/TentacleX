/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.codec.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
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
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.Frame;
import com.laxser.tentaclex.commons.spdy.frame.control.RstStream;
import com.laxser.tentaclex.commons.spdy.frame.control.SynReply;
import com.laxser.tentaclex.commons.spdy.frame.control.SynStream;

/**
 * @author laxser  Date 2012-6-1 上午8:43:47
@contact [duqifan@gmail.com]
@FrameResponseDecoder.java
 * 
 */
@ChannelPipelineCoverage("one")
public class FrameResponseDecoder extends SimpleChannelUpstreamHandler {

    private Map<Integer, List<Frame>> frameBuffer = new HashMap<Integer, List<Frame>>();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof Frame) {
            Frame frame = (Frame) e.getMessage();
            if (frame instanceof SynReply) {//这是要开启一个数据流呢
                SynReply sr = (SynReply) frame;
                Integer streamId = sr.getStreamId();
                if (frameBuffer.containsKey(streamId)) {//如果已经有了？出错
                    assert (false);
                    //TODO:出错处理
                }
                if ((sr.getFlags() & ControlFrame.FLAG_FIN) == 1) {//已经是最后一帧了
                    HttpResponse response = buildResponse(sr, null);
                    //交由下面处理
                    Channels.fireMessageReceived(ctx, response, e.getRemoteAddress());
                    //TODO:处理半关连接
                } else {//不是最后一帧，缓存住
                    //由于收到的是SynStream,必须为一个新的流，所以开新list
                    List<Frame> frameList = new LinkedList<Frame>();
                    frameList.add(sr);
                    frameBuffer.put(sr.getStreamId(), frameList);
                }
            } else if (frame instanceof RstStream) {
                //TODO:连接关闭

            } else if (frame instanceof DataFrame) {
                DataFrame dataFrame = (DataFrame) frame;
                if (frameBuffer.get(dataFrame.getStreamId()) == null) {
                    //不属于SynStream的DataFrame
                    super.messageReceived(ctx, e);
                    return;
                } else if ((dataFrame.getFlags() & ControlFrame.FLAG_FIN) == 1) {//最后一帧
                    List<Frame> list = frameBuffer.get(dataFrame.getStreamId());
                    SynReply headerFrame = (SynReply) list.get(0);//必须是头帧
                    List<DataFrame> dataList = new LinkedList<DataFrame>();
                    for (Frame frameElement : list) {
                        if (frameElement instanceof DataFrame) {//应该是必须的，这里只检查一下
                            dataList.add((DataFrame) frameElement);
                        }
                    }
                    dataList.add(dataFrame);
                    HttpResponse response = buildResponse(headerFrame, dataList);
                    //清理
                    frameBuffer.remove(headerFrame.getStreamId());
                    Channels.fireMessageReceived(ctx, response, e.getRemoteAddress());
                    //TODO:处理半关连接
                } else {//不是最后一帧，缓存
                    List<Frame> list = frameBuffer.get(dataFrame.getStreamId());
                    list.add(dataFrame);
                }
            } else if (frame instanceof SynStream) {
                //这是一个request，略过
                super.messageReceived(ctx, e);
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    private SpdyHttpResponse buildResponse(SynReply header, List<DataFrame> dataFrames) {
        Map<String, String> httpHeader = header.getNVMap();
        HttpVersion httpVersion = httpHeader.get("version").trim().equalsIgnoreCase("HTTP/1.1") ? HttpVersion.HTTP_1_1
                : HttpVersion.HTTP_1_0;
        int status = Integer.parseInt(httpHeader.get("status"));
        SpdyHttpResponse response = new SpdyHttpResponse(httpVersion, HttpResponseStatus
                .valueOf(status), header.getStreamId());

        for (Entry<String, String> entry : httpHeader.entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }

        if (dataFrames != null && dataFrames.size() > 0) {
            ChannelBuffer dataBuffer = ChannelBuffers.dynamicBuffer();
            for (DataFrame dataFrame : dataFrames) {
                dataBuffer.writeBytes(dataFrame.getData());

            }
            response.setContent(dataBuffer);
        }
        return response;
    }
}
