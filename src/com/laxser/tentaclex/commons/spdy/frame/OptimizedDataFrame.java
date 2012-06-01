package com.laxser.tentaclex.commons.spdy.frame;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * The optimized {@link DataFrame} implementation.
 * 
 *@author laxser  Date 2012-6-1 上午8:58:07
@contact [duqifan@gmail.com]
@OptimizedDataFrame.java

 */
public class OptimizedDataFrame extends DefaultFrame implements DataFrame{

    private static final long serialVersionUID = 1L;

    private static final int STREAMID_LENGTH_IN_BIT = 31;
    
    //private static final int CONTROL_BIT_CLEANER = (1 << STREAMID_LENGTH_IN_BIT) - 1;
    
    //private static final int CONTROL_BIT_SETTER = (1 << STREAMID_LENGTH_IN_BIT);
    
    private int streamId;

    private byte[] headerInBytes;
    
    private byte[] data;

    OptimizedDataFrame(int streamId, byte flags, byte[] data) {
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
    public boolean isControlFrame() {
    	return false;
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

    public byte[] headerToBytes() {
    	if (headerInBytes == null) {
    		headerInBytes = new byte[HEADER_LENGTH_IN_BYTE];
    	}
    	//streamId to byte[4], BIG_ENDIAN
    	byte[] streamIdInBytes = Utils.intToByte(getStreamId());
    	
    	/*
    	 * 只要streamId是正数，那么最高为就为0，也就是说对于DataFrame来说，Control bit为0，
    	 * 所以这里就不用再设置Control bit了
    	 * */
    	
    	//copy the first 4 bytes
    	System.arraycopy(streamIdInBytes, 0, headerInBytes, 0, streamIdInBytes.length);
    	//set flags
    	headerInBytes[4] = getFlags();
    	//设置长度字段，3个byte，高位被去掉了
    	byte[] lengthInBytes = Utils.intToByte(getLength());
		System.arraycopy(lengthInBytes, 1, headerInBytes, HEADER_LENGTH_IN_BYTE
				- LENGTH_LENGTH_IN_BYTE, LENGTH_LENGTH_IN_BYTE);
    	return headerInBytes;
    }
    
    @Override
    public byte[] getData() {
        return data;
    }

	@Override
	public ChannelBuffer toChannelBuffer() {
		return ChannelBuffers.wrappedBuffer(headerToBytes(), getData());
	}
}
