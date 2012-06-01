/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.laxser.tentaclex.commons.spdy.frame.Frame;

/**
 * 把Frame对象封装成netty的ChannelBuffer对象
 * 
 * @author laxser  Date 2012-5-31 下午4:57:45
@contact [duqifan@gmail.com]
@SPDYFrameEncoder.java

 */
@ChannelPipelineCoverage("one")
public class SPDYFrameEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg)
            throws Exception {
        if (msg instanceof Frame) {
            Frame frame = (Frame) msg;
            
            //如果Frame的实现做了toChannelBuffer的优化，就直接返回这个ChannelBuffer
            ChannelBuffer buffer = frame.toChannelBuffer();
            if (buffer != null) {
            	return buffer;
            } else {
            	return ChannelBuffers.wrappedBuffer(frame.toByte());
            }
        } else {
            return msg;
        }
    }
}
