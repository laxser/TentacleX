package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Arrays;
import java.util.Map;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.datastructure.NameValueBlock;

/**
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
 * @author laxser  Date 2012-6-1 上午8:59:41
@contact [duqifan@gmail.com]
@DefaultSynStream.java

 * 
 * @deprecated 被更优化的实现所取代
 */
public class DefaultSynStream extends SynStream {

    private static final long serialVersionUID = 1L;

    private NameValueBlock nameValueEntries;

    DefaultSynStream(byte flags, byte[] dataInByte) {
        super(flags, dataInByte);
    }
    
    DefaultSynStream(byte flags, int streamId) {
        this(flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }

    /*public SynStream(byte flags, int streamId) {
        super(ControlFrame.ControlFrameType.SYN_STREAM, flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }*/

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
    public byte[] getData() {
        byte[] sidInByte = Utils.intToByte(streamId);
        byte[] aidInByte = Utils.intToByte(associatedToStreamId);
        byte[] nvEntriesCount = Utils.intToByte(nameValueEntries.size());
        byte[] nvBody = nameValueEntries.toByteStructure();
        byte[] data = Utils.mergeBytes(sidInByte, aidInByte, nvEntriesCount, nvBody);
        return data;
    }

    @Override
    protected void buildDataFromByte(byte[] dataInByte) {
        if (dataInByte == null || dataInByte.length == 0) {
            //采用第二种构造方式，自己setXXX
            return;
        }
        byte[] streamIDInByte = Arrays.copyOfRange(dataInByte, 0, 4);

        streamIDInByte[0] = (byte) (streamIDInByte[0] & 0x7F);
        int _streamID = Utils.bytesToInt(streamIDInByte);

        byte[] associatedToStreamIDInByte = Arrays.copyOfRange(dataInByte, 4, 8);
        associatedToStreamIDInByte[0] = (byte) (associatedToStreamIDInByte[0] & 0x7F);
        int _associatedToStreamID = Utils.bytesToInt(associatedToStreamIDInByte);
        Utils.checkLength(_streamID, 31);
        Utils.checkLength(_associatedToStreamID, 31);
        this.streamId = _streamID;
        this.associatedToStreamId = _associatedToStreamID;
        //pri,unused,NVCount部分,无用
        //byte[] priAndUnusedAndNVCount = Utils.copyBytesFromHighPos(dataInByte, 4, 8);
        //NV部分,跳过Pri unused NVEntryCount4个字节
        byte[] nvData = Arrays.copyOfRange(dataInByte, 12, dataInByte.length);

        this.nameValueEntries = new NameValueBlock(nvData);
    }
} 
