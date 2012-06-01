package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Arrays;
import java.util.Map;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.datastructure.NameValueBlock;

/**
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
 * @author laxser  Date 2012-6-1 上午8:59:33
@contact [duqifan@gmail.com]
@DefaultSynReply.java

 * 
 * @deprecated 被更优化的实现所取代
 */
public class DefaultSynReply extends SynReply {

	private static final long serialVersionUID = 1L;

    //private int unused;

    private NameValueBlock nameValueEntries;

    //    public SynReply(byte flags, int streamID) {
    //        super(ControlFrame.ControlFrameType.SYN_REPLY, flags);
    //        Utils.checkLength(streamID, 31);
    //        this.streamID = streamID;
    //    }

    DefaultSynReply(byte flags, byte[] dataInByte) {
        super(flags, dataInByte);
    }

    DefaultSynReply(byte flags, int streamId) {
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
        byte[] idByte = Utils.intToByte(streamId);
        //这里忽略了NV前的unused字段
        byte[] nvEntriesCountInByte = Utils.intToByte(nameValueEntries.size());
        byte[] nvByte = nameValueEntries.toByteStructure();
        byte[] data = Utils.mergeBytes(idByte, nvEntriesCountInByte, nvByte);

        return data;
    }
    
    
    //TODO 实现getLength方法
    /*@Override
    public int getLength() {
    	return 0;
    }*/

    @Override
    protected void buildDataFromByte(byte[] dataInByte) {
        if (dataInByte == null || dataInByte.length == 0) {
            //采用第二种构造方式，自己setXXX
            return;
        }
        //streamId部分
        byte[] streamIDInByte = Arrays.copyOfRange(dataInByte, 0, 4);
        streamIDInByte[0] = (byte) (streamIDInByte[0] & 0x7F);
        int _streamID = Utils.bytesToInt(streamIDInByte);
        Utils.checkLength(_streamID, 31);
        this.streamId = _streamID;
        //unused,NVCount部分,无用
        //byte[] unusedAndNVCount = Utils.copyBytesFromHighPos(dataInByte, 4, 8);
        //NV部分
        byte[] nvData = Arrays.copyOfRange(dataInByte, 8, dataInByte.length);
        this.nameValueEntries = new NameValueBlock(nvData);
    }
    
} 
