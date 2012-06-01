package com.laxser.tentaclex.commons.spdy.frame;

/**
 * 和Stream相关的Frame，能通过streamId来进行区分
 * 
 * @author laxser  Date 2012-6-1 上午8:57:59
@contact [duqifan@gmail.com]
@StreamIdentifiableFrame.java

 */
public interface StreamIdentifiableFrame extends Frame{

	/**
	 * @return streamId
	 */
	public int getStreamId();
	
	/**
	 * 设置streamId
	 * @param streamId
	 */
	public void setStreamId(int streamId);
	
}
