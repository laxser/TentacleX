package com.laxser.tentaclex.registry;

import com.laxser.tentaclex.registry.impl.zookeeper.ZookeeperBasedRegistry;

/**
 * 
 * XoaRegistry工厂
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-25 上午11:42:55
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
