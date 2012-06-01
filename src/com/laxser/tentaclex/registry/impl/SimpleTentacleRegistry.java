package com.laxser.tentaclex.registry.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.laxser.tentaclex.registry.AbstractTentacleRegistry;
import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.registry.TentacleServiceDescriptorBase;

/**
 * 简单的XoaRegistry实现，可用于本地调试
 * 
 * @author laxser  Date 2012-6-1 上午8:47:10
@contact [duqifan@gmail.com]
@SimpleTentacleRegistry.java

 */
public class SimpleTentacleRegistry extends AbstractTentacleRegistry {
	
    private Map<String, Set<TentacleServiceDescriptor>> services = new HashMap<String, Set<TentacleServiceDescriptor>>();
	
	/**
	 * 向registry注册一个服务节点
	 * 
	 * @param serviceId
	 * @param ipAddress
	 * @param port
	 */
	public void register(String serviceId, String ipAddress, int port) {
		register(serviceId, ipAddress, port, false);
	}
	
	public void register(String serviceId, String ipAddress, int port, boolean disabled) {
        
	    Set<TentacleServiceDescriptor> nodes = services.get(serviceId);
        if (nodes == null) {
            nodes = new TreeSet<TentacleServiceDescriptor>(TentacleServiceDescriptor.COMPARATOR);
        }
	    TentacleServiceDescriptorBase desc = new TentacleServiceDescriptorBase();
        desc.setServiceId(serviceId).setIpAddress(ipAddress).setPort(port).setDisabled(disabled);
        nodes.add(desc);
        services.put(serviceId, nodes);
    }

	@Override
	public List<TentacleServiceDescriptor> getServiceNodes(String serviceId) {
		return new ArrayList<TentacleServiceDescriptor>(services.get(serviceId));
	}

	public void disableNode(String serviceId, String ip, int port) {
	    Set<TentacleServiceDescriptor> nodeSet = services.get(serviceId);
        TentacleServiceDescriptor toDisable = null;
        for (TentacleServiceDescriptor node : nodeSet) {
            if (node.getIpAddress().equals(ip) && node.getPort() == port) {
                toDisable = node;
            }
        }
        
        if (toDisable != null) {
            ((TentacleServiceDescriptorBase)toDisable).setDisabled(true);
            updateServiceNodes(serviceId, new ArrayList<TentacleServiceDescriptor>(nodeSet));
        }
	}
	
	public void deleteNode(String serviceId, String ip, int port) {
	    
	    Set<TentacleServiceDescriptor> nodeSet = services.get(serviceId);
	    TentacleServiceDescriptor toDelete = null;
	    for (TentacleServiceDescriptor node : nodeSet) {
	        if (node.getIpAddress().equals(ip) && node.getPort() == port) {
	            toDelete = node;
	        }
	    }
	    if (toDelete != null) {
	        nodeSet.remove(toDelete);
	        updateServiceNodes(serviceId, new ArrayList<TentacleServiceDescriptor>(nodeSet));
	    }
	}
	
	/**
     * 注册一个XoaServiceDescriptor
     * @param desc
     * @deprecated
     */
    public void register(TentacleServiceDescriptor desc) {
        throw new UnsupportedOperationException("This method is deprecated. " +
                "Use register(String serviceId, String ipAddress, int port) instead.");
    }
}
