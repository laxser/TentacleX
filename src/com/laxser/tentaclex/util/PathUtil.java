package com.laxser.tentaclex.util;

/**
 * 路径计算相关的工具类
 * 
 * @author laxser  Date 2012-6-1 上午8:47:27
@contact [duqifan@gmail.com]
@PathUtil.java

 */
public class PathUtil {

	public static final String SERVICE_NODE_FOLDER = "/.service-nodes";
	
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
