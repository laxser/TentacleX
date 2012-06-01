package com.laxser.tentaclex.commons.exception;


/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-5-31
 * time 下午4:56:15
 */
public class TentacleConnectException extends TentacleXException {

    private static final long serialVersionUID = 1;
	
    public TentacleConnectException() {

    }
    
    public TentacleConnectException(String msg) {
        super(msg);
    }

    public TentacleConnectException(Throwable cause) {
    	super(cause);
    }
    
    public TentacleConnectException(String msg, Throwable cause) {
    	super(msg, cause);
    }
}
