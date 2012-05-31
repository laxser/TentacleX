package com.laxser.tentaclex.methods;

import com.laxser.tentaclex.Method;

/**
 * {@link Method}的基类实现
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:35:46
 */
public abstract class TentacleMethodBase {/*implements Method {

	protected String serviceId;
	
	protected String path;
	
	protected Map<String, String[]> parameters;
	
	protected Map<String, String> headers;
	
	public XoaMethodBase(){}
	
	public XoaMethodBase(String serviceId, String path) {
		this.serviceId = serviceId;
		this.path = path;
	}
	
	@Override
	public String getServiceId() {
		return serviceId;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public void setParam(String name, String value) {
		if (name == null || value == null) {
			throw new NullPointerException();
		}
		if (parameters == null) {	//lazy init
			parameters = new HashMap<String, String[]>();
		}
		String[] values = parameters.get(name);
		if (values == null) {
			values = new String[]{value};
		} else {
			String[] newValues = new String[values.length + 1];
			System.arraycopy(values, 0, newValues, 0, values.length);
			newValues[newValues.length - 1] = value;	//put value at last
			values = newValues;
		}
		parameters.put(name, values);
	}
	
	@Override
	public void setHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	protected String urlEncode(String s) {
		
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			//code never reach here
			e.printStackTrace();
			return null;
		}
		
	}*/
	
}
