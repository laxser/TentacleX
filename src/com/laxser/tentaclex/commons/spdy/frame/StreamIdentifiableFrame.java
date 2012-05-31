package com.laxser.tentaclex.commons.spdy.frame;

/**
 * 和Stream相关的Frame，能通过streamId来进行区分
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-6-10 下午04:43:39
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
