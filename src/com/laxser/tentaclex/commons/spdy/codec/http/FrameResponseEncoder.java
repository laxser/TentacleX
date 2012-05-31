/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.codec.http;

import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.FrameFactory;
import com.laxser.tentaclex.commons.spdy.frame.control.SynReply;

/**
 * @author Lookis (lookisliu@gmail.com)
 * 
 */
@ChannelPipelineCoverage("one")
public class FrameResponseEncoder extends SimpleChannelDownstreamHandler {

    //private boolean isServer = true;

    //先默认为服务端
    //private AtomicInteger streamIdCreator = new AtomicInteger(2);

    //    public void bindRequested(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    //        isServer = false;
    //        streamIdCreator.set(1);
    //        super.bindRequested(ctx, e);
    //    };

	private FrameFactory frameFactory = FrameFactory.getInstance();
	
    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof SpdyHttpResponse) {

            SpdyHttpResponse responseToSend = (SpdyHttpResponse) e.getMessage();
            int streamId = responseToSend.getStreamId();

            long contentLength = responseToSend.getContentLength(); //从response头部中获取contentLength
            if (contentLength == 0) { //如果response头部中没有设置contentLength，则取buffer的length
                contentLength = responseToSend.getContent().readableBytes();
            }

            SynReply responseHeader = null;
            byte flags = 0;
            if (contentLength == 0) {//没有BODY的请求，只发送一个SynStream数据
                flags = (byte) (flags | ControlFrame.FLAG_FIN);
            }
            responseHeader = frameFactory.newSynReply(flags, streamId);
            Set<String> headerNames = responseToSend.getHeaderNames();

            HttpVersion version = responseToSend.getProtocolVersion();
            responseHeader.putNV("version", version.getText());
            responseHeader.putNV("status", Integer.toString(responseToSend.getStatus().getCode()));
            for (String name : headerNames) {
                responseHeader.putNV(name, responseToSend.getHeader(name));
            }
            //先发送Header
            Channels.write(ctx, e.getFuture(), responseHeader, e.getRemoteAddress());
            if (contentLength > 0) {
                //构造一堆的DataFrame来发送
                //TODO:以后加入dataFrame大小切分策略，现在就只发一个DataFrame
                ChannelBuffer content = responseToSend.getContent();
                flags = (byte) (flags | ControlFrame.FLAG_FIN);

                //copy data from content
                byte[] data = new byte[(int) contentLength];
                content.readBytes(data);

                //DataFrame dataFrame = new DefaultDataFrame(streamId, flags, data);
                DataFrame dataFrame = FrameFactory.getInstance().newDataFrame(streamId, flags, data);
                Channels.write(ctx, e.getFuture(), dataFrame, e.getRemoteAddress());
            }

        } else {
            super.writeRequested(ctx, e);
        }

    }
}
