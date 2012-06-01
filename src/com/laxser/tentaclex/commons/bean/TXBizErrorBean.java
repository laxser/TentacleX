package com.laxser.tentaclex.commons.bean;

/**
 * 用来封装业务中的错误信息，以返回给客户端
 * 
 * @author laxser  Date 2012-5-31 下午4:49:31
@contact [duqifan@gmail.com]
@TXBizErrorBean.java

 */
public class TXBizErrorBean {

	public static final int STATUS_CODE = 510;
	
	private int errorCode;
	
	private String message;
	
	public TXBizErrorBean() {
		
	}

	public TXBizErrorBean(int errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}
	
	public TXBizErrorBean(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
