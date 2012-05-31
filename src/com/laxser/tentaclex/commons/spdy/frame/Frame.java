package com.laxser.tentaclex.commons.spdy.frame;

import java.io.Serializable;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * 对应SPDY里的帧对象
 * 
 * @author Lookis (lookisliu@gmail.com)
 * 
 */
public interface Frame extends Serializable {

	//TODO 要不要改成byte类型？
    public static final int FLAG_FIN = 0x01;
    
    //TODO 要不要改成byte类型？
    public static final int FLAG_UNIDIRECTIONAL = 0x02;

    public static final int VERSION = 1;

    public boolean isControlFrame();

    public byte getFlags();

    /**
     * @return data部分的字节长度
     */
    public int getLength();

    public byte[] getData();

    @Deprecated
    public byte[] toByte();
    
    public ChannelBuffer toChannelBuffer();
}
