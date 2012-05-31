package com.laxser.tentaclex.commons.spdy.frame;

import org.jboss.netty.buffer.ChannelBuffer;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * The default {@link Frame} implementation.
 * 
 * @author Lookis (lookisliu@gmail.com)
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 */
public abstract class DefaultFrame implements Frame {

    private static final long serialVersionUID = 1L;

    //private byte[] data;

    protected byte flags;

    public static final int FLAGS_LENGTH_IN_BIT = 8;

    public static final int LENGTH_LENGTH_IN_BIT = 24;

    public static final int LENGTH_LENGTH_IN_BYTE = LENGTH_LENGTH_IN_BIT / 8;
    
    public static final int HEADER_LENGTH_IN_BYTE = 8;
    
    //
    //    int length;

    protected boolean controlFrame;

    public DefaultFrame(boolean isControlFrame, byte flags) {
        this.controlFrame = isControlFrame;
        Utils.checkLength(flags, LENGTH_LENGTH_IN_BIT);
        this.flags = flags;
    }

    //
    //    protected void setData(byte[] data) {
    //        this.data = data;
    //    }

    public byte getFlags() {
        return flags;
    }

    public int getLength() {
        return getData().length;
    }

    public boolean isControlFrame() {
        return controlFrame;
    }

    protected abstract byte[] getHeaderDifferentBlockByte();

    @Override
    public byte[] toByte() {
        byte[] headDifferentInByte = getHeaderDifferentBlockByte();

        byte[] flagInByte = Utils.intToByte(getFlags());
        byte[] fixSizeFlagInByte = Utils.trimBytes(flagInByte, 0, Utils
                .bitAlignToByte(FLAGS_LENGTH_IN_BIT));

        byte[] lengthInByte = Utils.intToByte(getLength());
        byte[] fixSizeLengthInByte = Utils.trimBytes(lengthInByte, 0, Utils
                .bitAlignToByte(LENGTH_LENGTH_IN_BIT));

        return Utils.mergeBytes(headDifferentInByte, fixSizeFlagInByte, fixSizeLengthInByte,
                getData());
    }
    
    @Override
    public ChannelBuffer toChannelBuffer() {
    	return null;
    }
} 
