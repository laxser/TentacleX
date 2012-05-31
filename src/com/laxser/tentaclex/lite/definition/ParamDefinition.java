package com.laxser.tentaclex.lite.definition;

/**
 * XOA方法中的参数定义
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-19 下午06:27:24
 */
public class ParamDefinition {
	
	/**
	 * 参数名
	 */
	private String paramName;
	
	/**
	 * 是XOA方法的第几个参数
	 */
	private int paramIndex;
	
	/**
	 * 参数传输时使用的数据类型，raw或者json
	 */
	private String type;
	
	public String getParamName() {
		return paramName;
	}
	void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public int getParamIndex() {
		return paramIndex;
	}
	void setParamIndex(int paramIndex) {
		this.paramIndex = paramIndex;
	}
	public String getType() {
		return type;
	}
	void setType(String type) {
		this.type = type;
	}
}
