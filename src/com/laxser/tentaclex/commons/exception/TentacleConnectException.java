package com.laxser.tentaclex.commons.exception;



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
