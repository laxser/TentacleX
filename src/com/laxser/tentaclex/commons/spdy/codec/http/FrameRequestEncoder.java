/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.codec.http;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.netty.SpdyHttpRequest;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.FrameFactory;
import com.laxser.tentaclex.commons.spdy.frame.control.SynStream;

/**
 * @author Lookis (lookisliu@gmail.com), Weibo Li
 * 
 */
@ChannelPipelineCoverage("one")
public class FrameRequestEncoder extends SimpleChannelDownstreamHandler {

	protected Log logger = LogFactory.getLog(this.getClass());
	
	private FrameFactory frameFactroy = FrameFactory.getInstance();
	
    //private boolean isServer = true;

    //先默认为服务端
    //private AtomicInteger streamIdCreator = new AtomicInteger(2);

    //    public void bindRequested(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    //        isServer = false;
    //        streamIdCreator.set(1);
    //        super.bindRequested(ctx, e);
    //    };

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof SpdyHttpRequest) {
            //int streamId = streamIdCreator.getAndAdd(2);
            SpdyHttpRequest requestToSend = (SpdyHttpRequest) e.getMessage();
            //requestToSend.setStreamId(streamId);//传给Tomcat，以用来传回
            SynStream requestHeader = null;
            byte flags = 0;
            
            int contentLength = requestToSend.getContent().readableBytes();
            if (contentLength == 0) {//没有BODY的请求，只发送一个SynStream数据
                flags = (byte) (flags | ControlFrame.FLAG_FIN);
            }
            requestHeader = frameFactroy.newSynStream(flags, requestToSend.getStreamId());
            Set<String> headerNames = requestToSend.getHeaderNames();

            HttpMethod method = requestToSend.getMethod();
            HttpVersion version = requestToSend.getProtocolVersion();
            String uri = requestToSend.getUri();
            requestHeader.putNV("method", method.getName());
            requestHeader.putNV("version", version.getText());
            requestHeader.putNV("url", uri);
            for (String name : headerNames) {
                requestHeader.putNV(name, requestToSend.getHeader(name));
            }
            //先发送Header
            ChannelFuture future = e.getFuture();
            Channels.write(ctx, future, requestHeader, e.getRemoteAddress());
            if (contentLength > 0) {
                //构造一堆的DataFrame来发送
                //TODO:以后加入dataFrame大小切分策略，现在就只发一个DataFrame
                ChannelBuffer content = requestToSend.getContent();
                flags = (byte) (flags | ControlFrame.FLAG_FIN);
                /*DataFrame dataFrame = new DefaultDataFrame(requestToSend.getStreamId(), flags,
                        content.toByteBuffer().array());*/
				DataFrame dataFrame = frameFactroy.newDataFrame(
						requestToSend.getStreamId(), flags,
						content.toByteBuffer().array());
                //future.awaitUninterruptibly();//等待前一个包发送完毕
                Channels.write(ctx, future, dataFrame, e.getRemoteAddress());
            }
        } else {
            super.writeRequested(ctx, e);
        }
    }
}
