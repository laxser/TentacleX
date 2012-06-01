package com.laxser.tentaclex.octopus.spdy.netty;

import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.laxser.tentaclex.commons.thread.NioThreadFactory;

/**
 * 
 * 这个类存在的意义在于封装唯一的NioClientSocketChannelFactory实例，
 * 保证不创建多余的IO线程
 * 
 * @author laxser  Date 2012-6-1 上午9:33:14
@contact [duqifan@gmail.com]
@ChannelFactoryManager.java

 */
public class ChannelFactoryManager {

	private static ChannelFactoryManager instance = new ChannelFactoryManager();
	
	public static ChannelFactoryManager getInstance() {
		return instance;
	}
	
	private NioClientSocketChannelFactory nioClientSocketChannelFactory;
	
	private ChannelFactoryManager() {
		try {
			init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
    private void init() {
        NioThreadFactory threadFactory = new NioThreadFactory();
        nioClientSocketChannelFactory = new NioClientSocketChannelFactory(Executors
                .newCachedThreadPool(threadFactory), Executors.newCachedThreadPool(threadFactory));
    }
	
	public ChannelFactory getClientChannelFactory() {
		return nioClientSocketChannelFactory;
	}
}
