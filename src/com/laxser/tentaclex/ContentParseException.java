package com.laxser.tentaclex;

import com.laxser.tentaclex.commons.exception.TentacleXException;

/**
 * 
 * 解析{@link TentacleResponse}的content发生错误是会抛出次异常
 * 
 * @author laxser  Date 2012-6-1 上午8:45:06
@contact [duqifan@gmail.com]
@ContentParseException.java

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
