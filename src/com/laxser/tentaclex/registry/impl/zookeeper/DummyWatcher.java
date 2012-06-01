package com.laxser.tentaclex.registry.impl.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


/**
 * 
 * 什么也不做的一个空的Watcher
 * 
 *@author laxser  Date 2012-6-1 上午8:47:18
@contact [duqifan@gmail.com]
@DummyWatcher.java

 */
public class DummyWatcher implements Watcher {

	@Override
	public void process(WatchedEvent event) {
		//do nothing
	}

}
