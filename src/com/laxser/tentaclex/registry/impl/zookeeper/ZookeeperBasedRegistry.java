package com.laxser.tentaclex.registry.impl.zookeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.laxser.tentaclex.registry.AbstractTentacleRegistry;
import com.laxser.tentaclex.registry.TentacleService;
import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.util.PathUtil;

/**
 * 基于Zookeeper的registry实现
 * 
 * @author laxser  Date 2012-6-1 上午8:47:38
@contact [duqifan@gmail.com]
@ZookeeperBasedRegistry.java

 */
public class ZookeeperBasedRegistry extends AbstractTentacleRegistry {
	
	private ZooKeeper zookeeper;
	
	//private ClientCheckinService checkinService;
	
	private ZookeeperWatcher zookeeperWatcher = new ZookeeperWatcher();
	
	public void init() {
		zookeeper = ZooKeeperFactory.newZooKeeper(zookeeperWatcher);
		//checkinService = new CheckinServiceImpl();
	}
	
	@Override
    public TentacleServiceDescriptor queryService(String serviceId) {
        return super.queryService(serviceId);
    }

    @Override
	public List<TentacleServiceDescriptor> getServiceNodes(String serviceId) {
		
		//根据给定的serviceId拼出存储在zookeeper中的配置文件的路径
		String nodePath = PathUtil.toZnodePath(serviceId);
		nodePath += PathUtil.SERVICE_NODE_FOLDER;
		
		try {
			Stat nodeStat = zookeeper.exists(nodePath, false);
			if (nodeStat == null) {
				logger.error("Path not exists: " + nodePath);
				return null;
			}
			
			//找到.service-nodes路径后，寻找其所有子节点
			List<String> children = zookeeper.getChildren(nodePath, zookeeperWatcher);
			List<TentacleServiceDescriptor> descs = new ArrayList<TentacleServiceDescriptor>();
			for (String child : children) {
				try {
					if(isValidIpPort(child)) {	//child是IP:Port
						byte[] data = zookeeper.getData(nodePath + "/" + child,
								zookeeperWatcher, null);
				
						String[] ss = child.split(":");
						String ip = ss[0];
						int port = Integer.parseInt(ss[1]);
						TentacleServiceDescriptor desc = RegistryHelper.fromBytes(serviceId, ip, port, data);
						descs.add(desc);
					} else {
						logger.warn("Unrecognized znode:" + nodePath + "/" + child);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return descs;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Watcher实现
	 * 
	 */
	private class ZookeeperWatcher implements Watcher {
		@Override
		public void process(WatchedEvent event) {
			
		    if (logger.isDebugEnabled()) {
                logger.debug("WatchedEvent received: " + event);
            }
		    
			String path = event.getPath();
	        if (event.getType() == Event.EventType.None) {
	            // We are are being told that the state of the
	            // connection has changed
	            switch (event.getState()) {
	            case SyncConnected:
	                if (logger.isInfoEnabled()) {
	                    logger.info("Zookeeper SyncConnected: " + event);
	                }
	                try {
	                    refreshZookeeperData();  //刷新数据, 防止重连时可能造成的信息丢失
                    } catch (Exception e) {
                        if (e instanceof ConnectionLossException
                                || (e.getCause() != null && e.getCause() instanceof ConnectionLossException)) {
                            
                            //如果是ConnectionLossException引起的，重连之
                            reInitZk(); //重连成功后会自动进入SyncConnected状态，所以会执行refreshZookeeperData()操作
                        } else {
                            logger.error("Error while refreshing data from ZooKeeper on event: " + event, e);
                        }
                    }
	                
	                break;
	            case Expired:
	            	logger.warn("Zookeeper session expired: " + event);
	            	reInitZk();   //重连成功后会自动进入SyncConnected状态，所以会执行refreshZookeeperData()操作
	                break;
	            }
	        } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
	        	if (path.endsWith(PathUtil.SERVICE_NODE_FOLDER)) {
					
	        		//这类事件的path应该类似: /com/renren/xoa/test/.service-nodes
	        		
	        		String serviceId = PathUtil.toServiceId(path.substring(0,
							path.length()
									- PathUtil.SERVICE_NODE_FOLDER.length()));
					if (logger.isInfoEnabled()) {
						logger.info("config changed for service: " + serviceId);
					}
					updateServiceNodes(serviceId, getServiceNodes(serviceId));
	        	} else {
	        		logger.warn("Unrecognized event:" + event);
	        	}
	        } else if (event.getType() == Event.EventType.NodeDeleted) {
	        	if (logger.isInfoEnabled()) {
	        		logger.info("node deleted: " + path);
	        	}
	        	
	        	//删除的事件可以不用处理，因为如果是某个节点的配置被删除的话，
	        	//同时还会触发Event.EventType.NodeDataChanged事件，所以
	        	//由那个分支去处理就可以了
	        	
	        } else if (event.getType() == Event.EventType.NodeDataChanged) {
	        	
	        	//这类事件的path应该类似: /com/renren/xoa/test/.service-nodes/10.3.1.1
	        	
	        	int index = path.indexOf(PathUtil.SERVICE_NODE_FOLDER);
	        	if (index > 0) {
	        		String serviceId = PathUtil.toServiceId(path.substring(0, index));
					if (logger.isInfoEnabled()) {
						logger.info("config changed for service: " + serviceId
								+ ", caused by node change:" + path);
					}
					updateServiceNodes(serviceId, getServiceNodes(serviceId));
	        	} else {
	        		logger.warn("Unrecognized event:" + event);
	        	}
	        } else {
	        	logger.warn("Unhandled event:" + event);
	        }
		}
	} 
	
	/**
     * 初始化一个新的Zookeeper实例，销毁掉老的
     */
    private void reInitZk() {
        ZooKeeper old = zookeeper;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("re-initializing ZooKeeper");
            }
            init(); //重新初始化一下，也就是重新连接一下
        } finally { //确保老的资源能顺利被释放
            if (logger.isInfoEnabled()) {
                logger.info("closing expired ZooKeeper:" + old.hashCode());
            }
            try {
                old.close();    //将旧的关闭掉
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * 刷新数据, 防止重连时可能造成的信息丢失
	 * 
	 * 刷新数据的时候会重新注册Watcher，也能解决Watcher丢失的问题
	 */
	private void refreshZookeeperData() {
	    //更新所有的服务节点信息
	    for (String serviceId : getServiceIds()) {
	        if (logger.isInfoEnabled()) {
	            logger.info("Refreshing data for service: " + serviceId);
	        }
	        updateServiceNodes(serviceId, getServiceNodes(serviceId));
	    }
	}
	
	private final Pattern ipPattern = Pattern.compile("[\\d]{1,3}(\\.[\\d]{1,3}){1,3}:[\\d]{1,5}");
	
	/**
	 * 匹配IP格式
	 * @param ip
	 * @return
	 */
	private boolean isValidIpPort(String ipPort) {
		return ipPattern.matcher(ipPort).matches();
	}
	
	/**
	 * 在Registry中查找出制定serviceId的配置
	 * 
	 * @param serviceId
	 * @return
	 */
	public TentacleService getService(String serviceId) {
		TentacleService svc = new TentacleService();
		svc.setServiceId(serviceId);
		List<TentacleServiceDescriptor> nodes = this.getServiceNodes(serviceId);
		svc.setNodes(nodes);
		return svc;
	}
	
	/**
	 * 返回Registry中注册的所有服务
	 * @return
	 */
	public List<TentacleService> getServices() {
		List<TentacleService> list = new ArrayList<TentacleService>();
		//从根据经开始深度搜索
		dfs("/", list);
		return list;
	}
	
	/**
	 * 在Zookeeper的文件目录中深度优先搜索所有的服务配置
	 * 
	 * @param nodePath
	 * @param list
	 */
	private void dfs(String nodePath, List<TentacleService> list) {
		try {
			Stat nodeStat = zookeeper.exists(nodePath, false);
			if (nodeStat == null) {
				return;
			}
			List<String> children = zookeeper.getChildren(nodePath, false);
			for (String child : children) {
				
				//计算当前路径
				String curPath = nodePath;
				if (!curPath.endsWith("/")) {
					curPath += "/";
				}
				curPath += child;
				
				if (curPath.endsWith(PathUtil.SERVICE_NODE_FOLDER)) {
					
					//在当前路径中找到了.service-nodes
					String serviceId = PathUtil.toServiceId(nodePath);
					if (logger.isDebugEnabled()) {
						logger.debug("service discovered:" + serviceId);
					}
					list.add(getService(serviceId));
				} else {
					//在当前路径中没有找到.service-nodes，继续向下搜索
					dfs(curPath, list);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 销毁资源
	 */
	public void destroy() {
	    try {
            zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
   /* private class CheckinServiceImpl extends AbstractCheckinService {

        @Override
        public boolean storeCheckin(String clientIp, String serviceId) {

            String path = PathUtil.toZnodePath(serviceId);
            path += "/" + clientIp;

            if (ZooKeeperUtil.exists(zookeeper, path)) {
                if (logger.isInfoEnabled()) {
                    logger.info("checkin already stored: " + serviceId + "-" + clientIp);
                }
                return true;
            } else {
                Properties prop = new Properties();

                prop.put("checkin.timestamp", Calendar.getInstance().getTime());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    prop.store(os, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ZooKeeperUtil.addData(zookeeper, path, os.toByteArray(),
                        CreateMode.EPHEMERAL);
            }
        }
    }
	
	public static void main(String[] args) {
        //System.out.println(Calendar.getInstance().getTime());
        
        Properties prop = new Properties();

        prop.put("checkin.timestamp", Calendar.getInstance().getTime().);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            prop.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }*/
}
