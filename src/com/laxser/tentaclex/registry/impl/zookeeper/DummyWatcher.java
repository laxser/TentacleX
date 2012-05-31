package com.laxser.tentaclex.registry.impl.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


/**
 * 
 * 什么也不做的一个空的Watcher
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-2-17 下午05:21:06
 */
public class DummyWatcher implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		//do nothing
	}

}
