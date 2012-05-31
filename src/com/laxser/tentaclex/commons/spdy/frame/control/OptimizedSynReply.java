package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.datastructure.NameValueBlock;

/**
 * 
 * 优化的SynReply实现
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
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 */
public class OptimizedSynReply extends SynReply {

	private static final long serialVersionUID = 1L;

    private NameValueBlock nameValueEntries;

    OptimizedSynReply(byte flags, byte[] dataInByte) {
        super(flags, dataInByte);
    }

    OptimizedSynReply(byte flags, int streamId) {
        super(flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }

    public String putNV(String name, String value) {
        return nameValueEntries.put(name, value);
    }

    public Map<String, String> getNVMap() {
        return nameValueEntries;
    }

    @Override
    public byte[] getData() {
        //byte[] idByte = Utils.intToByte(streamId);
        //这里忽略了NV前的unused字段
        //byte[] nvEntriesCountInByte = Utils.intToByte(nameValueEntries.size());
        //byte[] nvByte = nameValueEntries.toByteStructure();
        //byte[] data = Utils.mergeBytes(idByte, nvEntriesCountInByte, nvByte);
    	
    	//通过预先可以得到长度来new byte[]
    	byte[] data = new byte[getLength()]; 
    	//set streamId to data
    	byte[] streamIdInBytes = Utils.intToByte(streamId);
        System.arraycopy(streamIdInBytes, 0, data, 0, streamIdInBytes.length);
        
        //set nvEntriesCount to data
        byte[] nvEntriesCountInByte = Utils.intToByte(nameValueEntries.size());
        System.arraycopy(nvEntriesCountInByte, 0, data, 4, nvEntriesCountInByte.length);
    	
        //write nv entries to data
        nameValueEntries.writeToByteStructure(data, 8);
        
        return data;
    }

    @Override
	public ChannelBuffer toChannelBuffer() {
		return ChannelBuffers.wrappedBuffer(headerToBytes(), getData());
	}

	@Override
    public int getLength() {
    	/*
    	 * |X|          Stream-ID (31bits)    |
    	 * +----------------------------------+
    	 * | Unused        |    NV entries    |
    	 * +----------------------------------|
    	 * |     Name/value header block      |
    	 * |              ...                 |
    	 * */
    	//Data部分的长度就是8+Name/value header block
    	return 8 + this.nameValueEntries.getLength();
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
        //unused,NVCount部分,无用
        //byte[] unusedAndNVCount = Utils.copyBytesFromHighPos(dataInByte, 4, 8);
        //NV部分
        this.nameValueEntries = new NameValueBlock(dataInByte, 8, dataInByte.length - 8);
    }
} 
