package com.laxser.tentaclex.lite.definition;

/**
 * XOA请求的header的定义
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-19 下午06:26:09
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
