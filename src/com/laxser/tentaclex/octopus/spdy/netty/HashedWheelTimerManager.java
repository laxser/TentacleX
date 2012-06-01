package com.laxser.tentaclex.octopus.spdy.netty;

import org.jboss.netty.util.HashedWheelTimer;

/**
 * @author laxser  Date 2012-6-1 上午9:33:26
@contact [duqifan@gmail.com]
@HashedWheelTimerManager.java

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
