package com.laxser.tentaclex.registry;

/**
 * txServiceDescriptor的基�?���? * 
 * @author laxser  Date 2012-6-1 上午8:46:00
@contact [duqifan@gmail.com]
@TentacleServiceDescriptorBase.java

 */
public class TentacleServiceDescriptorBase implements TentacleServiceDescriptor{

	private String ipAddress;
	
	private String serviceId;
	
	private boolean disabled;
	
	private int port;

	public TentacleServiceDescriptorBase setServiceId(String serviceId) {
		this.serviceId = serviceId;
		return this;
	}

	public TentacleServiceDescriptorBase setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		return this;
	}

	public TentacleServiceDescriptorBase setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getIpAddress() {
		return ipAddress;
	}

	@Override
	public boolean isDisabled() {
		return this.disabled;
	}

	public TentacleServiceDescriptorBase setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	@Override
	public String toString() {
		return serviceId + "," + ipAddress + ":" + port + ","
				+ (disabled ? STATUS_DISABLED : STATUS_ENABLED);
	}
	
	
}
