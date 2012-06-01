package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.datastructure.NameValueBlock;

/**
 * 优化的SynStream实现
 * 
 * <pre>
 *   +----------------------------------+
 *   |1|       1          |       1     |
 *   +----------------------------------+
 *   | Flags (8)  |  Length (24 bits)   |
 *   +----------------------------------+
 *   |X|          Stream-ID (31bits)    |
 *   +----------------------------------+
 *   |X|Associated-To-Stream-ID (31bits)|
 *   +----------------------------------+
 *   | Pri | Unused    |   NV Entries   |
 *   +----------------------------------|
 *   |     Name/value header block      |
 *   |             ...                  |
 * </pre>
 * 
 * @author laxser  Date 2012-6-1 上午9:01:56
@contact [duqifan@gmail.com]
@OptimizedSynStream.java

 */
public class OptimizedSynStream extends SynStream {

    private static final long serialVersionUID = 1L;

    //    private int priority;
    //
    //    private int unused;

    private NameValueBlock nameValueEntries;

    OptimizedSynStream(byte flags, byte[] dataInByte) {
        super(flags, dataInByte);
    }
    
    OptimizedSynStream(byte flags, int streamId) {
        this(flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }

    @Override
    public String putNV(String name, String value) {
        return nameValueEntries.put(name, value);
    }

    @Override
    public String get(String name) {
        return nameValueEntries.get(name);
    }

    @Override
    public Map<String, String> getNVMap() {
        return nameValueEntries;
    }
    
    @Override
    public int getLength() {
    	/*
    	 *   |X|          Stream-ID (31bits)    |
    	 *   +----------------------------------+
    	 *   |X|Associated-To-Stream-ID (31bits)|
    	 *   +----------------------------------+
    	 *   | Pri | Unused    |   NV Entries   |
    	 *   +----------------------------------|
    	 *   |     Name/value header block      |
    	 *   |             ...                  |
    	 * */
    	//Data部分的长度就是12+Name/value header block
    	return 12 + this.nameValueEntries.getLength();
    }

    @Override
    public byte[] getData() {
        /*byte[] sidInByte = Utils.intToByte(streamId);
        byte[] aidInByte = Utils.intToByte(associatedToStreamId);
        byte[] nvEntriesCount = Utils.intToByte(nameValueEntries.size());
        byte[] nvBody = nameValueEntries.toByteStructure();
        byte[] data = Utils.mergeBytes(sidInByte, aidInByte, nvEntriesCount, nvBody);
        return data;*/
    	
    	byte[] data = new byte[getLength()];
    	
    	//set streamId to data
    	byte[] streamIdInBytes = Utils.intToByte(streamId);
        System.arraycopy(streamIdInBytes, 0, data, 0, streamIdInBytes.length);
        
        //set associatedToStreamId to data
    	byte[] associatedToStreamIdInBytes = Utils.intToByte(associatedToStreamId);
        System.arraycopy(associatedToStreamIdInBytes, 0, data, 4, associatedToStreamIdInBytes.length);
        
        //set nvEntriesCount to data
        byte[] nvEntriesCountInByte = Utils.intToByte(nameValueEntries.size());
        System.arraycopy(nvEntriesCountInByte, 0, data, 8, nvEntriesCountInByte.length);
    	
        //write nv entries to data
        nameValueEntries.writeToByteStructure(data, 12);
        
        return data;
    }

    @Override
	public ChannelBuffer toChannelBuffer() {
		return ChannelBuffers.wrappedBuffer(headerToBytes(), getData());
	}
    
    @Override
    protected void buildDataFromByte(byte[] dataInByte) {
        if (dataInByte == null || dataInByte.length == 0) {
            //采用第二种构造方式，自己setXXX
            return;
        }
        
        //streamId部分
        int _streamID = Utils.bytesToInt(dataInByte, 0, 4);
        Utils.checkLength(_streamID, 31);
        this.streamId = _streamID;
        
        //associatedToStreamId部分
        int _associatedToStreamId = Utils.bytesToInt(dataInByte, 4, 4);
        Utils.checkLength(_associatedToStreamId, 31);
        this.associatedToStreamId = _associatedToStreamId;
        
        //unused,NVCount部分,无用
        //byte[] unusedAndNVCount = Utils.copyBytesFromHighPos(dataInByte, 4, 8);
        //NV部分
        this.nameValueEntries = new NameValueBlock(dataInByte, 12, dataInByte.length - 12);
        
    }
} 
