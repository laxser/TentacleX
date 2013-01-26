package com.laxser.tentaclex.octopus.service;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.laxser.tentaclex.commons.exception.TentacleConnectException;
import com.laxser.tentaclex.octopus.spdy.PowerfulSpdyClient;

/**
 * 
 * @author laxser  Date 2012-6-1 上午8:54:16
@contact [duqifan@gmail.com]
@SpdyClientManager.java

 */
public class SpdyClientManager {

    private Log logger = LogFactory.getLog(this.getClass());
    
	private static SpdyClientManager instance = new SpdyClientManager();
	
	private Map<String, SpdyClient> clientPool = new ConcurrentHashMap<String, SpdyClient>();
	
	public static SpdyClientManager getInstance() {
		return instance;
	}
	
	private Random random = new Random();
	
	private SpdyClientManager() {
		
	}
	
	/**
	 * 获取一个连接指定host的client
	 * @param hostport
	 * @return
	 */
	public SpdyClient getClient(String hostport){
        if (clientPool.get(hostport) == null) { //DCL
            //构造新的Service
            synchronized (hostport.intern()) {
                if (clientPool.get(hostport) == null) {
                    SpdyClient txService = initClient(hostport);
                    clientPool.put(hostport, txService);
                }
            }
        }
        return clientPool.get(hostport);
    }

	
	/**
	 * 从给定的hostport列表中选取一个健康的
	 * @param hostports
	 * @return
	 */
	public SpdyClient findHealthyClient(List<String> hostports) {
		SpdyClient[] clients = new SpdyClient[hostports.size()];
		int offset = 0;
		for (String host : hostports) {
			SpdyClient client = clientPool.get(host);
			if (client != null && client.isConnected()) {
				clients[offset++] = client;
			}
		}
		if (offset == 0) {
			throw new TentacleConnectException("No healthy communication client available");
		}
		int ranIndex = random.nextInt(offset);
		return clients[ranIndex];
	}
	
	/**
	 * 初始化一个到质量host的client
	 * @param hostport
	 * @return
	 */
	private SpdyClient initClient(String hostport) {
		String[] ss = hostport.split(":");
		if (ss.length != 2) {
			throw new IllegalArgumentException(hostport);
		}
		String hostname = ss[0];
		int port = Integer.parseInt(ss[1]);
		PowerfulSpdyClient spdyClient = new PowerfulSpdyClient(new InetSocketAddress(hostname, port));
		spdyClient.init();
        return spdyClient;
	}
	
    /**
     * 销毁掉指定的hostport所对应的{@link SpdyClient}
     * @param hostport
     */
    public void destroyClient(String hostport) {
        SpdyClient client = clientPool.remove(hostport);
        if (client == null) {
            logger.error(SpdyClient.class.getSimpleName() + " not in pool: to "
                    + hostport);
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Destroying " + SpdyClient.class.getSimpleName() + " connecting with "
                        + hostport + ": " + client);
            }
            client.disconnect();
        }
    }
}
