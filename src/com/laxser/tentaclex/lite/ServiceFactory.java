package com.laxser.tentaclex.lite;

import com.laxser.tentaclex.Tentacle;

/**
 * 对Service接口进行封装的工厂
 * 
 * @author laxser  Date 2012-6-1 上午8:55:39
@contact [duqifan@gmail.com]
@ServiceFactory.java

 */
public interface ServiceFactory {
	 
	public <T> T getService(Class<T> serviceInterface);
	
	public <T> T getService(Class<T> serviceInterface, Tentacle client);
	
}
