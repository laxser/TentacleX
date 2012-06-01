package com.laxser.tentaclex.lite;

/**
 * {@link ServiceFactory}的工厂，以及封装相关工具方法。
 * 
 * @author laxser  Date 2012-6-1 上午8:55:47
@contact [duqifan@gmail.com]
@ServiceFactories.java

 */
public class ServiceFactories {

	private static ServiceFactory defaultFactory = new DefaultServiceFactory();
	
	/**
	 * @return 默认的ServiceFactory
	 */
	public static ServiceFactory getFactory() {
		return defaultFactory;
	}
	
}
