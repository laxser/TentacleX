package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * 
 * SynReply的一种实现，这种实现的Data部分，即NV block相关的部分在构造后
 * 即为只读了，可以在proxy场景下使用此实现以提高性能。
 * 
 * <pre>
 *   +----------------------------------+
 *   |1|        1        |        2     |
 *   +----------------------------------+
 *   | Flags (8)  |  Length (24 bits)   |
 *   +----------------------------------+
 *   |X|          Stream-ID (31bits)    |
 *   +----------------------------------+
 *   | Unused        |    NV entries    |
 *   +----------------------------------|
 *   |     Name/value header block      |
 *   |              ...                 |
 * </pre>
 * 
 * @author laxser  Date 2012-6-4 下午1:53:36
@contact [duqifan@gmail.com]
@NVBlockReadonlySynReply.java

 */
public class NVBlockReadonlySynReply extends SynReply {

	private static final long serialVersionUID = 1L;

    //private int unused;

    //    public SynReply(byte flags, int streamID) {
    //        super(ControlFrame.ControlFrameType.SYN_REPLY, flags);
    //        Utils.checkLength(streamID, 31);
    //        this.streamID = streamID;
    //    }

	private byte[] data;
	
    NVBlockReadonlySynReply(byte flags, byte[] dataInByte) {
        super(flags, dataInByte);
        this.data = dataInByte;
        
        //由于streamId来类的streamId字段中有冗余，所以构造的时候要设置一下
		super.setStreamId(Utils.bytesToInt(data, 0, 4));
    }

    @Override
    public String putNV(String name, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getNVMap() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getData() {
    	return data;
    }
    
    @Override
	public ChannelBuffer toChannelBuffer() {
		return ChannelBuffers.wrappedBuffer(headerToBytes(), getData());
	}

    @Override
    public void setStreamId(int streamId) {
    	super.setStreamId(streamId);
    	byte[] streamIdInBytes = Utils.intToByte(streamId); 
    	//更改data中的streamId
    	System.arraycopy(streamIdInBytes, 0, data, 0, streamIdInBytes.length);
    }
    
	@Override
    protected void buildDataFromByte(byte[] dataInByte) {
		//do nothing
		//由于此实现data部分只读，所以没必要再还原成NVBlock
    }
} 
