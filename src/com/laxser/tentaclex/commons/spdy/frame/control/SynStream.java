package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Map;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;

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
 * @author laxser  Date 2012-6-1 上午9:00:31
@contact [duqifan@gmail.com]
@SynStream.java

 */
public abstract class SynStream extends StreamIdentifiableControlFrame {

    private static final long serialVersionUID = 1L;

    protected static final byte[] HEADER_FIRST_4_BYTES = {-128, 1, 0, 1};
    
    //Server Push 
    protected int associatedToStreamId;

    //    private int priority;
    //
    //    private int unused;

    //private NameValueBlock nameValueEntries;

    SynStream(byte flags, byte[] dataInByte) {
        super(ControlFrame.ControlFrameType.SYN_STREAM, flags, dataInByte);
    }

    public abstract String putNV(String name, String value);
    
    public abstract String get(String name);
    
    public abstract Map<String, String> getNVMap();
    
    public byte[] headerToBytes() {
		byte[] header = new byte[8];
		System.arraycopy(HEADER_FIRST_4_BYTES, 0, header, 0,
				HEADER_FIRST_4_BYTES.length);
		header[4] = this.flags;
		byte[] lengthInBytes = Utils.intToByte(getLength());
		System.arraycopy(lengthInBytes, 1, header, 5, 3);
		return header;
	}
    
    /*public SynStream(byte flags, int streamId) {
        super(ControlFrame.ControlFrameType.SYN_STREAM, flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }

    public String putNV(String name, String value) {
        return nameValueEntries.put(name, value);
    }

    public String get(String name) {
        return nameValueEntries.get(name);
    }

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
    }*/
}
