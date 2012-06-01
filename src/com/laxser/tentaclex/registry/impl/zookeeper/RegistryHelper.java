package com.laxser.tentaclex.registry.impl.zookeeper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.registry.TentacleServiceDescriptorBase;
import com.laxser.tentaclex.util.PathUtil;
import com.laxser.tentaclex.util.ZooKeeperUtil;

/**
 * Registry相关的工具方法封装
 * 
 * @author laxser  Date 2012-6-1 上午8:47:32
@contact [duqifan@gmail.com]
@RegistryHelper.java

 */
public class RegistryHelper {
    
	private static Log logger = LogFactory.getLog(RegistryHelper.class);
	
	private ZooKeeper zookeeper;
	
	private Watcher watcher = new DummyWatcher();
	
	/**
	 * 当前实例是否创建了新的ZooKeeper实例。
	 * 
	 * 如果创建了新的ZooKeeper实例，在destroy的时候需要同步销毁ZooKeeper
	 */
	private final boolean isZooKeeperContructed;
	
	public RegistryHelper() {
	    zookeeper = ZooKeeperFactory.newZooKeeper(watcher);
	    isZooKeeperContructed = true;
    }
	
	public RegistryHelper(ZooKeeper zk) {
	    this.zookeeper = zk;
	    isZooKeeperContructed = false;
	}
	
	/**
	 * 向Registry新注册一个节点，如果该节点已经存在了，操作会失败。
	 * 
	 * @param desc
	 */
	public boolean registerServiceNode(TentacleServiceDescriptor desc)  {
		try {
			String path = toConfigFilePath(desc);
			byte[] data = toBytes(desc);
			boolean succ = ZooKeeperUtil.addData(zookeeper, path, data);
			if (succ) {
				logger.info("Register successful, path: " + path + ", data:\r\n"
						+ new String(data));
				return true;
			} else {
				logger.error("Register failed, path: " + path + ", data:\r\n"
						+ new String(data));
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 在Registry中更新一个节点的信息，如果该节点不存在，操作会失败。
	 * 
	 * @param desc
	 * @return
	 */
	public boolean updateServiceNode(TentacleServiceDescriptor desc)  {
		try {
			String path = toConfigFilePath(desc);
			
			if (zookeeper.exists(path, watcher) == null) {
				logger.error("Node config not exists, your should register one first: " + path);
				return false;
			}
			byte[] data = toBytes(desc);
			boolean succ = ZooKeeperUtil.setData(zookeeper, path, data);
			if (succ) {
				logger.info("Update successful, path: " + path + ", data:\r\n"
						+ new String(data));
				return true;
			} else {
				logger.info("Update failed, path: " + path + ", data:\r\n"
						+ new String(data));
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除指定的服务就节点
	 * 
	 * @param desc
	 * @return
	 */
	public boolean deleteServiceNode(TentacleServiceDescriptor desc) {
		try {
			String path = toConfigFilePath(desc);
			if (zookeeper.exists(path, watcher) == null) {
				logger.error("Node config not exists: " + path);
				return false;
			}
			boolean succ = ZooKeeperUtil.delete(zookeeper, path);
			if (succ) {
				logger.info("Delete successful, path: " + path );
				return true;
			} else {
				logger.info("Delete failed, path: " + path);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean exists(TentacleServiceDescriptor desc) {
	    try {
            String path = toConfigFilePath(desc);
            if (zookeeper.exists(path, watcher) != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return false;
	}
	
	public static String toConfigFilePath(String serviceId, String ip, int port) {
	    StringBuilder path = new StringBuilder();
        path.append(PathUtil.toZnodePath(serviceId));
        path.append(PathUtil.SERVICE_NODE_FOLDER);
        path.append("/");
        path.append(ip);
        path.append(":");
        path.append(port);
        return path.toString();
	}
	
	public static String toConfigFilePath(TentacleServiceDescriptor desc) {
		return toConfigFilePath(desc.getServiceId(), desc.getIpAddress(), desc.getPort());
	}
	
	/**
	 * 将给定的XoaServiceDescriptor转化为properties配置
	 * 格式的byte数组
	 * 
	 * @param desc
	 * @return
	 */
	public static byte[] toBytes(TentacleServiceDescriptor desc) {
		Properties prop = new Properties();
		prop.put("status",
				desc.isDisabled() ? TentacleServiceDescriptor.STATUS_DISABLED
						: TentacleServiceDescriptor.STATUS_ENABLED);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			prop.store(os, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	
	/**
	 * 给定serviceId、ip、port和配置文件path，获取XoaServiceDescriptor对象
	 * 
	 * @param serviceId
	 * @param ip
	 * @param port
	 * @param path
	 * @return
	 */
	public TentacleServiceDescriptor getNodeConfig(String serviceId, String ip, int port) {
	    String path = toConfigFilePath(serviceId, ip, port);
	    try {
	        if (zookeeper.exists(path, watcher) != null) {
	            byte[] data = zookeeper.getData(path, watcher, null);
	            return fromBytes(serviceId, ip, port, data);
	        } else {
	            return null;
	        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * 给定serviceId、ip、port和data，获取XoaServiceDescriptor对象
	 * 
	 * @param serviceId
	 * @param ip
	 * @param port
	 * @param data
	 * @return
	 */
	public static TentacleServiceDescriptor fromBytes(String serviceId, String ip, int port, byte[] data) {
	  //获取其他属性
        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(data));
            String status = (String) properties.getProperty("status");
            boolean disabled = TentacleServiceDescriptor.STATUS_DISABLED.equals(status);
            
            TentacleServiceDescriptorBase desc = new TentacleServiceDescriptorBase();
            desc.setServiceId(serviceId).setIpAddress(ip)
                    .setPort(port).setDisabled(disabled);
            return desc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	/**
	 * 销毁持有的资源
	 */
	public void destroy() {
	    if (isZooKeeperContructed) {   //当前实例是否创建了新的ZooKeeper实例
	        try {
                zookeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	    }
	}
}
