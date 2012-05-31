package com.laxser.tentaclex.lite.definition;

/**
 * 一个XOA service的定义
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-17 下午12:04:33
 */
public class ServiceDefinition {

	/**
	 * service id
	 */
	private String serviceId;

	/**
	 * 可以指定本次调用的host列表，以ip:port的形式，
	 * 如果指定了hosts，那么registry中的配置会被覆盖
	 */
	//private String[] hosts;
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/*public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}*/
}
