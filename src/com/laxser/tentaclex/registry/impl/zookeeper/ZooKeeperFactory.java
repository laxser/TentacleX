package com.laxser.tentaclex.registry.impl.zookeeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 统一封装获取ZooKeeper实例的逻辑
 * 
 * @author laxser  Date 2012-6-1 上午8:47:43
@contact [duqifan@gmail.com]
@ZooKeeperFactory.java

 */
public class ZooKeeperFactory {
	
	static String HOSTS = null;
	
	static String ROOT_PATH = null;
	
	static {
		loadConnectString();
	}
	
	static final String CONFIG_FILE_NAME = "connect-conf.properties";
	
	private static final int DEFUALT_ZOOKEEPER_SESSION_TIMEOUT = 30000;

	private static Log logger = LogFactory.getLog(ZooKeeperFactory.class);
	
	public static ZooKeeper newZooKeeper(Watcher watcher) {
		return newZooKeeper(getConnectString(), watcher);
	}
	
	public static ZooKeeper newZooKeeper(String connectString, Watcher watcher) {
		try {
			ZooKeeper zookeeper = new ZooKeeper(connectString,
					DEFUALT_ZOOKEEPER_SESSION_TIMEOUT, watcher);
			return zookeeper;
		} catch (IOException e) {
			throw new RuntimeException("Error occurs while creating ZooKeeper instance.", e);
		}
	}
	
	static void loadConnectString() {
		InputStream is = ZooKeeperFactory.class.getResourceAsStream(CONFIG_FILE_NAME);
		try {
			
			Properties prop = new Properties();
			prop.load(is);
			
			String hosts = (String)prop.get("hosts");
			if (hosts == null) {
				throw new RuntimeException("Need conf for zookeeper hosts.");
			}
			String rootPath = (String)prop.get("rootPath");
			if (rootPath == null) {
				throw new RuntimeException("Need conf for zookeeper rootPath.");
			}
			if (HOSTS != null) {
				logger.warn("Somewhere overrides the host config to: " + HOSTS);
			} else {
				HOSTS = hosts;
			}
			ROOT_PATH = rootPath;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static String getConnectString() {
		return HOSTS + ROOT_PATH;
	}
	
	public static void main(String[] args) {
		System.out.println(getConnectString());
	}
	
}
