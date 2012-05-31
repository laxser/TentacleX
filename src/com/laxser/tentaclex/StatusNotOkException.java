package com.laxser.tentaclex;

import com.laxser.tentaclex.commons.exception.TentacleXException;

/**
 * 
 * XOA调用返回的{@link TentacleResponse}的status code不是200(OK)时抛出此异常
 * 
 * @author Li Weibo (weibo.li@opi-corp.com) //I believe spring-brother
 * @since 2010-3-29 下午04:56:05
 */
public class StatusNotOkException extends TentacleXException {

	private static final long serialVersionUID = 1L;

	/**
	 * 这个异常所包装的XoaResponse对象
	 */
	private TentacleResponse response;
	
	public StatusNotOkException() {
		super();
	}
	
	public StatusNotOkException(String msg) {
		super(msg);
	}
	
	public StatusNotOkException(Throwable cause) {
		super(cause);
	}
	
	public StatusNotOkException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TentacleResponse getResponse() {
		return response;
	}

	public StatusNotOkException setResponse(TentacleResponse response) {
		this.response = response;
		return this;
	}
}
