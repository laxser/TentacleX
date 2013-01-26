package com.laxser.tentaclex.registry;

import java.util.Comparator;
import java.util.List;

/**
 * 封装�?��tx服务的配置信�? * 
 *@author laxser  Date 2012-6-1 上午8:46:14
@contact [duqifan@gmail.com]
@TentacleService.java

 */
public class TentacleService {

	private String serviceId;
	
	private List<TentacleServiceDescriptor> nodes;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public List<TentacleServiceDescriptor> getNodes() {
		return nodes;
	}

	public void setNodes(List<TentacleServiceDescriptor> nodes) {
		this.nodes = nodes;
	}
	
	public static final Comparator<TentacleService> COMPARATOR = new Comparator<TentacleService>() {
		@Override
		public int compare(TentacleService o1, TentacleService o2) {
			return o1.getServiceId().compareTo(o2.getServiceId());
		}
	};
	
}
