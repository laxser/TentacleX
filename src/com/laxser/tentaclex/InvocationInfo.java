package com.laxser.tentaclex;

import java.io.Serializable;
/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:44:47
 */
public interface InvocationInfo extends Serializable {
	
	public String getUrl();
	
	public String getMethodName();
	
	public String getRemoteHost();
	
}
