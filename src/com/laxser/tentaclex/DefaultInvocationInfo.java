package com.laxser.tentaclex;

public class DefaultInvocationInfo implements InvocationInfo {

	private static final long serialVersionUID = 1L;

	private String url;
	
	private String methodName;
	
	private String remoteHost;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
}
