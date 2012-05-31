package com.laxser.tentaclex.octopus.spdy.netty;

import org.jboss.netty.util.HashedWheelTimer;

/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-5-24 下午03:09:25
 */
public class HashedWheelTimerManager {

	private static HashedWheelTimerManager instance = new HashedWheelTimerManager();
	
	private HashedWheelTimer timer = new HashedWheelTimer();
	
	public static HashedWheelTimerManager getInstance() {
		return instance;
	}
	
	private HashedWheelTimerManager() {
	}
	
	public HashedWheelTimer getTimer() {
		return timer;
	}
	
}
