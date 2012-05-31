/**
 * 
 */
package com.laxser.tentaclex.commons.exception;

/**
 * @author Lookis (lookisliu@gmail.com)
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
