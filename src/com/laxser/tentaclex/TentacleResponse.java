package com.laxser.tentaclex;



/**
 * 表示一次tx调用的返回值
 * 
 * @author laxser  Date 2012-6-1 上午8:44:18
@contact [duqifan@gmail.com]
@TentacleResponse.java

 */
public interface TentacleResponse {

	/**
	 * @return 此次调用的状态码
	 */
	public int getStatusCode();
	
	/**
	 * @return 标识响应此次请求的远程主机
	 */
	public String getRemoteHost();
	
	/**
	 * 返回指定应答头部
	 * @param headerName
	 * @return
	 */
	public String getHeader(String headerName);
	
	
	/**
	 * 将返回值封装成指定的JavaBean对象类型并返回
	 * 
	 * @param <T> JavaBean的类反省
	 * @param klass JavaBean的类
	 * @return 指定的bean对象
	 * @throws ContentParseException 内容解析错误时抛出此异常
	 */
	public <T> T getContentAs(Class<T> klass) throws ContentParseException;
	
	/**
	 * 以String的形式返回内容
	 * @return
	 */
	public String getContentAsString();
}
