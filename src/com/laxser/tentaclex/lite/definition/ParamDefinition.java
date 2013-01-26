package com.laxser.tentaclex.lite.definition;

/**
 * tx方法中的参数定义
 * 
 *@author laxser  Date 2012-6-1 上午8:56:32
@contact [duqifan@gmail.com]
@ParamDefinition.java

 */
public class ParamDefinition {
	
	/**
	 * 参数名
	 */
	private String paramName;
	
	/**
	 * 是tx方法的第几个参数
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
