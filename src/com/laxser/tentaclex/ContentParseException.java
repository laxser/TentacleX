package com.laxser.tentaclex;

import com.laxser.tentaclex.commons.exception.TentacleXException;

/**
 * 
 * 解析{@link TentacleResponse}的content发生错误是会抛出次异常
 * 
 * @author Li Weibo (weibo.li@opi-corp.com) //I believe spring-brother
 * @since 2010-3-29 下午04:56:05
 */
public class ContentParseException extends TentacleXException {

	private static final long serialVersionUID = 1L;

	public ContentParseException() {
		super();
	}
	
	public ContentParseException(String msg) {
		super(msg);
	}
	
	public ContentParseException(Throwable cause) {
		super(cause);
	}
	
	public ContentParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
