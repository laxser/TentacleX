package com.laxser.tentaclex.registry;

import java.util.List;

/**
 * 
 * @author laxser  Date 2012-6-1 上午8:46:32
@contact [duqifan@gmail.com]
@TentacleRegistry.java

 */
public interface TentacleRegistry {
	
	/**
	 * 返回�?��服务节点
	 * @param serviceId
	 * @return
	 */
	public TentacleServiceDescriptor queryService(String serviceId);
	
	/**
	 * 返回Registry注册的所有服务节�?	 * @param serviceId
	 * @return
	 */
	public List<TentacleServiceDescriptor> queryServices(String serviceId);
	
	/**
	 * 注册事件监听�?	 * @param listener
	 */
	public void addListener(TentacleRegistryListener listener);
	
	/**
	 * 查询指定的ip和port正在为那些serviceId提供服务
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public List<String> lookup(String ip, int port);
}
