package com.laxser.tentaclex;
/**
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:44:58
 */
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
