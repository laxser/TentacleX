package com.laxser.tentaclex.registry;

import com.laxser.tentaclex.registry.impl.zookeeper.ZookeeperBasedRegistry;

/**
 * 
 * XoaRegistry工厂
 * 
 *@author laxser  Date 2012-6-1 上午8:46:26
@contact [duqifan@gmail.com]
@TentacleRegistryFactory.java

 */
public class TentacleRegistryFactory {

	private static TentacleRegistryFactory instance = new TentacleRegistryFactory();
	
	public static TentacleRegistryFactory getInstance() {
		return instance;
	}
	
	private TentacleRegistry registry;
	
	private TentacleRegistryFactory(){
		try {
			ZookeeperBasedRegistry reg = new ZookeeperBasedRegistry();
			reg.init();
			this.registry = reg;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return 默认的XoaRegistry
	 */
	public TentacleRegistry getDefaultRegistry() {
		return registry;
	}
}
