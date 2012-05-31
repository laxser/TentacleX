package com.laxser.tentaclex.commons.spdy.frame;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * The default {@link DataFrame} implementation.
 * 
 * @author Lookis (lookisliu@gmail.com)
 * 
 * @deprecated 被更优化的实现所取代
 * 
 */
public class DefaultDataFrame extends DefaultFrame implements DataFrame{

    private static final long serialVersionUID = 1L;

    private static final int STREAMID_LENGTH_IN_BIT = 31;

    private int streamId;

    private byte[] data;

    DefaultDataFrame(int streamId, byte flags, byte[] data) {
        super(false, flags);
        this.streamId = streamId;
        this.data = data;
    }

    @Override
    public int getStreamId() {
        return this.streamId;
    }

    @Override
	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}
    
    @Override
    protected byte[] getHeaderDifferentBlockByte() {
        byte[] streamIdInByte = Utils.intToByte(getStreamId());
        //换算成字节数
        int streamIdLengthInByte = Utils.bitAlignToByte(STREAMID_LENGTH_IN_BIT);

        byte[] fixSizeStreamIDInByte = Utils.trimBytes(streamIdInByte, 0, streamIdLengthInByte);
        byte[] fixSizeStreamIDInByteWithControlBit = fixSizeStreamIDInByte;
        fixSizeStreamIDInByteWithControlBit[0] = (byte) (fixSizeStreamIDInByteWithControlBit[0] & ((1 << STREAMID_LENGTH_IN_BIT) - 1));//最高位清0
        if (isControlFrame()) {
            fixSizeStreamIDInByteWithControlBit[0] = (byte) (fixSizeStreamIDInByteWithControlBit[0] | (1 << STREAMID_LENGTH_IN_BIT));//最高位置对应的数据
        }
        return fixSizeStreamIDInByteWithControlBit;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
