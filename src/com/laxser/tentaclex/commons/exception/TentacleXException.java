/**
 * 
 */
package com.laxser.tentaclex.commons.exception;

/**
 * @author laxser  Date 2012-5-31 下午4:47:19
@contact [duqifan@gmail.com]
@TentacleXException.java

 * 
 */
public class TentacleXException extends RuntimeException {

    private static final long serialVersionUID = -8100054656748896265L;

    public TentacleXException() {
        super();
    }

    public TentacleXException(String msg) {
        super(msg);
    }
    
    public TentacleXException(Throwable cause) {
		super(cause);
	}
    
    public TentacleXException(String msg, Throwable cause) {
    	super(msg, cause);
    }

}
