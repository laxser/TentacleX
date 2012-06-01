package com.laxser.tentaclex.util;

import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

/**
 * ZooKeeper相关工具
 * 
 * @author laxser  Date 2012-6-1 上午8:47:50
@contact [duqifan@gmail.com]
@ZooKeeperUtil.java

 */
public class ZooKeeperUtil {

	private static Log logger = LogFactory.getLog(ZooKeeperUtil.class);
	
	public static final int NO_VERSION = -1; // -1是指不关心version

	/**
	 * 向目标path设置数据，如果目标path不存在，就创建之
	 * 
	 * @param zookeeper
	 * @param path
	 * @param data
	 * @return
	 */
	public static boolean setData(ZooKeeper zookeeper, String path, byte[] data) {

		try {
			Stat stat = zookeeper.exists(path, false);
			if (stat == null) { // node not exists
				zookeeper.create(path, data, Collections.singletonList(new ACL(
						Perms.ALL, new Id("world", "anyone"))),
						CreateMode.PERSISTENT);
			} else { // node exists
				zookeeper.setData(path, data, NO_VERSION);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean exists(ZooKeeper zookeeper, String path) {
	    try {
            Stat stat = zookeeper.exists(path, false);
            return stat != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
	
	public static boolean addData(ZooKeeper zookeeper, String path, byte[] data, CreateMode createMode) {
	    
	    if (createMode == null) {
	        createMode = CreateMode.PERSISTENT;
	    }
	    
	    try {
            Stat stat = zookeeper.exists(path, false);
            if (stat == null) { // node not exists
                
                String[] tokens = path.split("/");
                
                StringBuilder currentPathBuff = new StringBuilder();
                for (String token : tokens) {
                    if (token.length() > 0) {   //第一个token可能是空串
                        currentPathBuff.append("/");
                        currentPathBuff.append(token);
                        
                        String currentPath = currentPathBuff.toString();
                        if (zookeeper.exists(currentPath, false) == null) { //当前路径不存在
                            if (currentPath.equals(path)) { //如果是要设置的节点
                                zookeeper.create(currentPath, data, Collections.singletonList(new ACL(
                                        Perms.ALL, new Id("world", "anyone"))),
                                        createMode);
                            } else {
                                //如果是中间的路径，则创建之，使用data使用空byte数组
                                zookeeper.create(currentPath, new byte[0], Collections.singletonList(new ACL(
                                        Perms.ALL, new Id("world", "anyone"))),
                                        createMode);
                            }
                        }
                    }
                }
            } else { // node exists
                logger.error(path + " already exists.");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}
	
	/**
	 * 向目标path设置添加数据，如果目标path不存在，就创建之；
	 * 如果目标path已经存在了，则什么也不做，返回false。
	 * 
	 * @param zookeeper
	 * @param path
	 * @param data
	 * @return
	 */
	public static boolean addData(ZooKeeper zookeeper, String path, byte[] data) {
	    return addData(zookeeper, path, data, CreateMode.PERSISTENT);
		/*try {
			Stat stat = zookeeper.exists(path, false);
			if (stat == null) { // node not exists
				
				String[] tokens = path.split("/");
				
				StringBuilder currentPathBuff = new StringBuilder();
				for (String token : tokens) {
					if (token.length() > 0) {	//第一个token可能是空串
						currentPathBuff.append("/");
						currentPathBuff.append(token);
						
						String currentPath = currentPathBuff.toString();
						if (zookeeper.exists(currentPath, false) == null) {	//当前路径不存在
							if (currentPath.equals(path)) {	//如果是要设置的节点
								zookeeper.create(currentPath, data, Collections.singletonList(new ACL(
										Perms.ALL, new Id("world", "anyone"))),
										CreateMode.PERSISTENT);
							} else {
								//如果是中间的路径，则创建之，使用data使用空byte数组
								zookeeper.create(currentPath, new byte[0], Collections.singletonList(new ACL(
										Perms.ALL, new Id("world", "anyone"))),
										CreateMode.PERSISTENT);
							}
						}
					}
				}
			} else { // node exists
				logger.error(path + " already exists.");
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;*/
	}
	
	public static boolean delete(ZooKeeper zookeeper, String path) {
		try {
			Stat stat = zookeeper.exists(path, false);
			if (stat == null) { // node not exists
				logger.error("path not exists: " + path);
				return false;
			} else { // node exists
				zookeeper.delete(path, NO_VERSION);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
