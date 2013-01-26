package com.laxser.tentaclex.lite.definition;

/**
 * tx请求的header的定义
 * 
 * @author laxser  Date 2012-6-1 上午8:56:21
@contact [duqifan@gmail.com]
@HeaderDefinition.java

 */
public class HeaderDefinition {
	
	/**
	 * header name
	 */
	private String headerName;
	
	/**
	 * 是方法的第几个参数
	 */
	private int paramIndex;
	
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public int getParamIndex() {
		return paramIndex;
	}
	public void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}
	
}
