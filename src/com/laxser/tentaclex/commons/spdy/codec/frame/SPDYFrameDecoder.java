package com.laxser.tentaclex.commons.spdy.codec.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.frame.DefaultFrame;
import com.laxser.tentaclex.commons.spdy.frame.FrameFactory;

/**
 * 把ChannelBuffer对象还原为Frame对象
 * 
 * @author laxser  Date 2012-5-31 下午4:57:29
@contact [duqifan@gmail.com]
@SPDYFrameDecoder.java

 */
public class SPDYFrameDecoder extends FrameDecoder {

	private boolean forProxy = false;
	
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception {
        //读取Frame头

        if (buffer.readableBytes() < 8) {
            return null;
        }
        byte[] header = new byte[8];
        buffer.markReaderIndex();
        buffer.readBytes(header);
        byte[] lengthInByte = Utils.trimBytes(header, 0, Utils
                .bitAlignToByte(DefaultFrame.LENGTH_LENGTH_IN_BIT));

        int length = Utils.bytesToInt(lengthInByte);
        //读取data
        if (buffer.readableBytes() < length) {
            //数据不够就重置读取指针，以便下一次重新读
            buffer.resetReaderIndex();
            return null;
        }
        byte[] data = new byte[length];
        buffer.readBytes(data);
        //frame读取完毕，构造Frame
        return FrameFactory.getInstance().buildFromByte(header, data, forProxy);
    }

	/**
	 * 设置当前{@link SPDYFrameDecoder}在proxy模式下运行
	 * @param forProxy
	 * @return
	 */
	public SPDYFrameDecoder setForProxy(boolean forProxy) {
		this.forProxy = forProxy;
		return this;
	}
} 
