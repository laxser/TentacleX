package com.laxser.tentaclex.registry.impl.zookeeper;

/**
 * 路径计算相关的工具类
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-2-20 上午11:21:12
 */
public class PathUtil {

	static final String SERVICE_NODE_FOLDER = "/.service-nodes";
	
	/**
	 * 给定serviceId，计算出其对应的path，相对路径
	 * @param serviceId
	 * @return
	 */
	public static String toZnodePath(String serviceId) {
		String[] ss = serviceId.split("\\.");
		StringBuilder sb = new StringBuilder(50);
		for(int i = ss.length - 1; i >= 0; i--) {
			sb.append("/");
			sb.append(ss[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 给定相对的path，计算出serviceId
	 * 
	 * @param znodePath
	 * @return
	 */
	public static String toServiceId(String znodePath) {
		String[] ss = znodePath.split("/");
		StringBuilder sb = new StringBuilder(znodePath.length());
		for(int i = ss.length - 1; i >= 0; i--) {
			if (ss[i].length() > 0) {
				sb.append(ss[i]);
				sb.append(".");
			}
		}
		if (sb.charAt(sb.length() - 1) == '.') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
}
