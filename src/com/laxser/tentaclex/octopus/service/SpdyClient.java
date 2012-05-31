package com.laxser.tentaclex.octopus.service;

import com.laxser.tentaclex.commons.netty.SpdyHttpRequest;

/**
 * 
 * @author Lookis (lookisliu@gmail.com)
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * 
 */
public interface SpdyClient {

	/**
	 * @return 远程host
	 */
	public String getRemoteHost();
	
    /**
     * 发送请求
     * @param request
     * @param ob
     */
    public void send(SpdyHttpRequest request, ResponseObserver ob);

    /**
     * @return 当前client是否与远程主机建立了可用的连接
     */
    public boolean isConnected();
    
    /**
     * 与远程服务器断开连接，断开连接后当前{@link SpdyClient}对象将无法
     * 继续使用。
     */
    public void disconnect();
    
}
