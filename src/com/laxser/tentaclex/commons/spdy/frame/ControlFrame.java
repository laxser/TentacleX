package com.laxser.tentaclex.commons.spdy.frame;

/**
 * 控制帧
 * 
 * @author laxser  Date 2012-6-1 上午8:59:25
@contact [duqifan@gmail.com]
@ControlFrame.java

 * 
 */
public interface ControlFrame extends Frame {

    /*
     * 第一版仅支持 SYN_STREAM,SYN_REPLY,RST_STREAM
     */
    public static enum ControlFrameType {
        SYN_STREAM(1), SYN_REPLY(2), RST_STREAM(3), //
        HELLO(4), NOOP(5), PING(6), GOAWAY(7), HEADERS(8);

        private int value;

        ControlFrameType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public int getVersion();

    public ControlFrameType getType();
}
