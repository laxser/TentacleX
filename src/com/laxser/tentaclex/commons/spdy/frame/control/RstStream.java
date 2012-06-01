/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.frame.control;

import java.util.Arrays;

import com.laxser.tentaclex.commons.spdy.Utils;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DefaultControlFrame;

/**
 * <pre>
 *   +-------------------------------+
 *   |1|       1        |      3     |
 *   +-------------------------------+
 *   | Flags (8)  |         8        |
 *   +-------------------------------+
 *   |X|          Stream-ID (31bits) |
 *   +-------------------------------+
 *   |          Status code          | 
 *   +-------------------------------+
 * </pre>
 * 
 *@author laxser  Date 2012-6-1 上午9:01:48
@contact [duqifan@gmail.com]
@RstStream.java

 * 
 */
public class RstStream extends DefaultControlFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 2323841155922744046L;

    public static enum StatusCode {
        PROTOCOL_ERROR(1), INVALID_STREAM(2), REFUSED_STREAM(3), UNSUPPORTED_VERSION(4), CANCEL(5), INTERNAL_ERROR(
                6);

        private int value;

        StatusCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private StatusCode statusCode;

    private int streamID;

    public RstStream(byte flags, byte[] dataInByte) {
        super(ControlFrame.ControlFrameType.RST_STREAM, flags, dataInByte);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public int getStreamID() {
        return streamID;
    }

    @Override
    public byte[] getData() {
        //这里直接忽略了StreamID前的保留位
        //TODO：做好处理
        byte[] idByte = Utils.intToByte(streamID);
        byte[] codeByte = Utils.intToByte(statusCode.getValue());
        byte[] data = Utils.mergeBytes(idByte, codeByte);
        return data;
    }

    @Override
    protected void buildDataFromByte(byte[] dataInByte) {

        byte[] streamIDInByte = Arrays.copyOfRange(dataInByte, 0, 4);
        //fix X bit
        streamIDInByte[0] = (byte) (streamIDInByte[0] & 0x7F);
        int _streamID = Utils.bytesToInt(streamIDInByte);
        Utils.checkLength(_streamID, 31);
        this.streamID = _streamID;

        StatusCode _statusCode = null;
        byte[] statusCodeInByte = Arrays.copyOfRange(dataInByte, 4, 8);
        int statusCodeInInteger = Utils.bytesToInt(statusCodeInByte);
        for (StatusCode status : StatusCode.values()) {
            if (status.getValue() == statusCodeInInteger) {
                _statusCode = status;
                break;
            }
        }
        this.statusCode = _statusCode;
    }
} 
