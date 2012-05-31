/**
 * 
 */
package com.laxser.tentaclex.commons.spdy.frame;

import com.laxser.tentaclex.commons.spdy.Utils;

/**
 * The default {@link ControlFrame} implementation.
 * 
 * @author Lookis (lookisliu@gmail.com)
 * 
 */
public abstract class DefaultControlFrame extends DefaultFrame implements ControlFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 4422989150442190496L;

    private static final int VERSION_LENGTH_IN_BIT = 15;

    private static final int TYPE_LENGTH_IN_BIT = 16;

    private int version = Frame.VERSION;

    private ControlFrameType type;

    public DefaultControlFrame(ControlFrameType type, byte flags, byte[] dataInByte) {
        super(true, flags);
        this.type = type;
        buildDataFromByte(dataInByte);
    }

    @Override
    public ControlFrameType getType() {
        return type;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    protected byte[] getHeaderDifferentBlockByte() {
        byte[] versionInByte = Utils.intToByte(getVersion());
        //换算成字节数
        int versionLengthInByte = Utils.bitAlignToByte(VERSION_LENGTH_IN_BIT);

        int typeLengthInByte = Utils.bitAlignToByte(TYPE_LENGTH_IN_BIT);

        byte[] fixSizeVersionInByte = Utils.trimBytes(versionInByte, 0, versionLengthInByte);
        byte[] fixSizeVersionInByteWithControlBit = fixSizeVersionInByte;
        fixSizeVersionInByteWithControlBit[0] = (byte) (fixSizeVersionInByteWithControlBit[0] & (0x7F));//最高位清0
        if (isControlFrame()) {
            fixSizeVersionInByteWithControlBit[0] = (byte) (fixSizeVersionInByteWithControlBit[0] | (0x80));//最高位置对应的数据
        }
        byte[] typeInByte = Utils.intToByte(getType().getValue());
        byte[] fixSizeTypeInByte = Utils.trimBytes(typeInByte, 0, typeLengthInByte);

        return Utils.mergeBytes(fixSizeVersionInByteWithControlBit, fixSizeTypeInByte);

    }

    protected abstract void buildDataFromByte(byte[] dataInByte);
}
