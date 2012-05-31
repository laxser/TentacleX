package com.laxser.tentaclex.lite;

/**
 * {@link ServiceFactory}的工厂，以及封装相关工具方法。
 * 
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-26 下午04:35:39
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
