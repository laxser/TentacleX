package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Map;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;

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
 * @author Lookis (lookisliu@gmail.com)
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 */
public abstract class SynReply extends StreamIdentifiableControlFrame {

	private static final long serialVersionUID = 1L;

	protected static final byte[] HEADER_FIRST_4_BYTES = {-128, 1, 0, 2};
	
    //private int unused;

    //private NameValueBlock nameValueEntries;

    //    public SynReply(byte flags, int streamID) {
    //        super(ControlFrame.ControlFrameType.SYN_REPLY, flags);
    //        Utils.checkLength(streamID, 31);
    //        this.streamID = streamID;
    //    }

	SynReply(byte flags, byte[] dataInByte) {
        super(ControlFrame.ControlFrameType.SYN_REPLY, flags, dataInByte);
    }
	
    /*SynReply(byte flags, byte[] dataInByte) {
        super(ControlFrame.ControlFrameType.SYN_REPLY, flags, dataInByte);
    }

    SynReply(byte flags, int streamId) {
        super(ControlFrame.ControlFrameType.SYN_REPLY, flags, null);
        setStreamId(streamId);
        nameValueEntries = new NameValueBlock();
    }*/

    public abstract String putNV(String name, String value);

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
    
    /*@Override
    public byte[] getData() {
        byte[] idByte = Utils.intToByte(streamId);
        //这里忽略了NV前的unused字段
        byte[] nvEntriesCountInByte = Utils.intToByte(nameValueEntries.size());
        byte[] nvByte = nameValueEntries.toByteStructure();
        byte[] data = Utils.mergeBytes(idByte, nvEntriesCountInByte, nvByte);

        return data;
    }

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
    }*/
}
