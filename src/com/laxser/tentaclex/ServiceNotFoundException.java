package com.laxser.tentaclex;

import com.laxser.tentaclex.commons.exception.TentacleXException;

/**
 * Thrown when service not found for specified serviceId. 
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-5-28 下午03:11:45
 */
public class ServiceNotFoundException extends TentacleXException {

	private static final long serialVersionUID = 1L;

	public ServiceNotFoundException() {
		super();
	}
	
	public ServiceNotFoundException(String msg) {
		super(msg);
	}
	
	public ServiceNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public ServiceNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
