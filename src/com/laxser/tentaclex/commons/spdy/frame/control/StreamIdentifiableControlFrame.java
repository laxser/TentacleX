package com.laxser.tentaclex.commons.spdy.frame.control;

import com.laxser.tentaclex.commons.spdy.frame.ControlFrame.ControlFrameType;
import com.laxser.tentaclex.commons.spdy.frame.DefaultControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.StreamIdentifiableFrame;

/**
 * 和Stream相关的，能通过streamId来唯一确定的ControlFrame
 * 
 * @author laxser  Date 2012-6-1 上午9:00:48
@contact [duqifan@gmail.com]
@StreamIdentifiableControlFrame.java

 */
public abstract class StreamIdentifiableControlFrame extends DefaultControlFrame implements
		StreamIdentifiableFrame {

	private static final long serialVersionUID = 1L;
	
	public StreamIdentifiableControlFrame(ControlFrameType type, byte flags, byte[] dataInByte) {
		super(type, flags, dataInByte);
	}

	protected int streamId;

	@Override
	public int getStreamId() {
		return streamId;
	}

	@Override
	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}
}
