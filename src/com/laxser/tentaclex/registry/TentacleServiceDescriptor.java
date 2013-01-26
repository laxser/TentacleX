package com.laxser.tentaclex.registry;

import java.util.Comparator;

/**
 * tx服务描述符，用以表示提供服务的真实机器的IP地址和端�? * 
 * @author laxser  Date 2012-6-1 上午8:46:07
@contact [duqifan@gmail.com]
@TentacleServiceDescriptor.java

 */
public interface TentacleServiceDescriptor {
	
	public static final String STATUS_ENABLED = "enabled";
	
	public static final String STATUS_DISABLED = "disabled";
	
	/**
	 * @return 服务器的ip地址
	 */
	public String getIpAddress();
	
	/**
	 * @return 要访问的服务的ID
	 */
	public String getServiceId();
	
	/**
	 * @return 服务端口
	 */
	public int getPort();
	
	/**
	 * @return 是否被置为不可用状�?
	 */
	public boolean isDisabled();
	
	public static final Comparator<TentacleServiceDescriptor> COMPARATOR = new Comparator<TentacleServiceDescriptor>() {

		@Override
		public int compare(TentacleServiceDescriptor o1, TentacleServiceDescriptor o2) {
			
			//先比较serviceId
			int cmp = o1.getServiceId().compareTo(o2.getServiceId());
			if (cmp == 0) {	//serviceId相同
				
				//再比IP地址
				cmp = o1.getIpAddress().compareTo(o2.getIpAddress());
				if (cmp == 0) {	//ip也相�?					
					//�?��比端�? 因为端口是正整数，且�?���?5545，所以直接减不用担心下溢
					return o1.getPort() - o2.getPort();
				} else {
					return cmp;
				}
			} else {
				return cmp;
			}
		}
	};
	
}
