package com.laxser.tentaclex;

import java.io.Serializable;

public interface InvocationInfo extends Serializable {
	
	public String getUrl();
	
	public String getMethodName();
	
	public String getRemoteHost();
	
}
