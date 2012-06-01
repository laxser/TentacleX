package com.laxser.tentaclex;

import com.laxser.tentaclex.commons.exception.TentacleXException;

/**
 * Thrown when service not found for specified serviceId. 
 * 
 * @author laxser  Date 2012-6-1 上午8:44:36
@contact [duqifan@gmail.com]
@ServiceNotFoundException.java

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
