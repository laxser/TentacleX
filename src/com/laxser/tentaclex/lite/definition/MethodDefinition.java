package com.laxser.tentaclex.lite.definition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XOA方法的定义
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-17 下午12:04:57
 */
public class MethodDefinition {

	/**
	 * 所定义的方法的本体
	 */
	private Method ownerMethod;
	
	/**
	 * 方法类型：GET/PUT/POST/DELETE
	 */
	private String method;
	
	/**
	 * 资源URI
	 */
	private String uri;
	
	private String contentType;
	
	/**
	 * 返回值类型
	 */
	private Type returnType;
	
	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	private Map<String, ParamDefinition> paramDefinitions = new HashMap<String, ParamDefinition>();
	
	private List<HeaderDefinition> headerDefinitions = new ArrayList<HeaderDefinition>();
	
	public Method getOwnerMethod() {
		return ownerMethod;
	}

	public void setOwnerMethod(Method ownerMethod) {
		this.ownerMethod = ownerMethod;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Iterator<ParamDefinition> paramDefinitions(){
		return this.paramDefinitions.values().iterator();
	}
	
	public ParamDefinition getParamDefinition(String paramName) {
		return paramDefinitions.get(paramName);
	}
	
	public Iterator<HeaderDefinition> headerDefinitions(){
		return this.headerDefinitions.iterator();
	}
	
	public void addParamDefinition(String paramName, int paramIndex, String type) {
		ParamDefinition paramDef = new ParamDefinition();
		paramDef.setParamIndex(paramIndex);
		paramDef.setParamName(paramName);
		paramDef.setType(type);
		paramDefinitions.put(paramName, paramDef);
	}
	
	public void addHeaderDefinition(String headerName, int paramIndex) {
		HeaderDefinition def = new HeaderDefinition();
		def.setHeaderName(headerName);
		def.setParamIndex(paramIndex);
		headerDefinitions.add(def);
	}
	
}
