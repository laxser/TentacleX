package com.laxser.tentaclex;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import com.laxser.tentaclex.jackson.ObjectMapperFactory;
import com.laxser.tentaclex.methods.TentacleDeleteMethod;
import com.laxser.tentaclex.methods.TentacleGetMethod;
import com.laxser.tentaclex.methods.TentacleMethodName;
import com.laxser.tentaclex.methods.TentacleMultiFormatPostMethod;
import com.laxser.tentaclex.methods.TentaclePostMethod;

/**
 * 用来表示要调用的XOA方法。
 * 
 * 方法的定位采用了REST的设计思想。
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 上午11:47:52
 */
public abstract class Method {
	
	public static final String ACCEPT_TEXT_PLAIN = "text/plain";
	
	public static final String ACCEPT_JSON = "application/json";
	
	public static final String ACCEPT_SERIALIZABLE = "application/serializable";
	
	public static final String ACCEPT_PROTOC_BUFF = "application/protoc-buff";
	
	protected String serviceId;
	
	private int port;
	
	protected String path;
	
	protected Map<String, String[]> parameters;
	
	protected Map<String, String> headers;
	
	public Method(){}
	
	/*public Method(String serviceId, int port, String path) {
		this.port = port;
		this.serviceId = serviceId;
		this.path = path;
	}*/
	
	public static Method get(String url) {
		Method method = new TentacleGetMethod();
		resolveUrl(url, method);
		return method;
	}
	
	public static Method put(String url) {
		
		//借助服务器端rose的特性，用post方法来伪造put方法，
		//解决put方法没有body的问题
		Method method = new TentaclePostMethod();
		resolveUrl(url, method);
		
		//给path中增加_method参数
		String path = method.getPath();
		if (path.indexOf('?') >= 0) {
			path += "&";
		} else {
			path += "?";
		}
		path += "_method=put";
		method.setPath(path);
		
		return method;
	}
	
	public static Method post(String url) {
		Method method = new TentaclePostMethod();
		resolveUrl(url, method);
		return method;
	}
	
	public static Method multiFormatPost(String url) {
        Method method = new TentacleMultiFormatPostMethod();
        resolveUrl(url, method);
        return method;
    }
	
	public static Method delete(String url) {
		Method method = new TentacleDeleteMethod();
		resolveUrl(url, method);
		return method;
	}
	
	private static void resolveUrl(String url, Method method) {
		
		//由于xoa并不是标准的URL协议，所以不能用jdk自带的URL类来解析
		
		int index = url.indexOf("://");
		//String protocol = "xoa";
		if (index != -1) {
			//protocol = url.substring(0, index);
			url = url.substring(index + 3);
		}
		
		//System.out.println(protocol);
		
		index = url.indexOf("/");
		String path = "";
		if (index != -1) {
			path = url.substring(index);
			url = url.substring(0, index);
		}
		method.setPath(path);
		//System.out.println(path);
		
		int port = -1;
		String host = url;
		index = url.indexOf(":");
		if (index != -1) {
			port = Integer.parseInt(url.substring(index + 1));
			host = url.substring(0, index);
		}
		method.setServiceId(host);
		method.setPort(port);
		//System.out.println(host);
		//System.out.println(port);
	}
	
	/**
     * 获取当前方法的名字，如GET,POST,PUT,DELETE
     * 
     * @return the name of this method
     */
    public abstract TentacleMethodName getName();
	
	/**
	 * 此XOA方法要访问的service的id，也就是要访问的虚机名(不包括端口)
	 * 如blog.xoa.renren.com
	 * 
	 * @param serviceId
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	/**
	 * @return 此XOA方法要访问的service的id
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * 此方法所在路径，如：/blog/user/1234，同时这个路径中是可以带query string的，
	 * 如：/blog/user/1234?type=1。
	 * 
	 * domain和path一起就构成了此方法的URL
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return 此方法所在路径
	 */
	public String getPath() {
		return path;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 增加一个参数，同一个参数名可以有多个参数值
	 * @param name 参数名
	 * @param value 参数值
	 */
	public void setParam(String name, String value) {
		if (name == null || value == null) {
			return;
			//throw new NullPointerException();
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
	
	/**
	 * 增加一个JaveBean作为参数，参数在传输的时中会被转化为json。
	 * 在服务端需要将此json反解回javabean对象，目前可以使用JsonParam
	 * 标注来实现。
	 * 
	 * @param name
	 * @param value
	 */
	public void setParamAsJson(String name, Object value) {
		ObjectMapper mapper = ObjectMapperFactory.getInstance().getObjectMapper();
		try {
			String jsonValue = mapper.writeValueAsString(value);
			this.setParam(name, jsonValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 设置一个请求的header
	 * @param name
	 * @param value
	 */
	public void setHeader(String name, String value) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public void setAcceptHeader(String... accepts) {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		headers.put(HttpHeaders.Names.ACCEPT, buildAcceptHeaderValue(accepts));
	}
	
	static String buildAcceptHeaderValue(String... accepts) {
		
		if (accepts.length == 0) {
			return "";
		}
		if (accepts.length == 1) {
			return accepts[0];
		}
		StringBuilder sb = new StringBuilder();
		for (String a : accepts) {
			sb.append(a);
			sb.append(',');
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
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
	}
	
	public String getUrl() {
		StringBuilder sb = new StringBuilder(50);
		sb.append("xoa://");
		sb.append(serviceId);
		if (port != -1) {
			sb.append(":");
			sb.append(port);
		}
		sb.append(path);
		return sb.toString();
	}
}
