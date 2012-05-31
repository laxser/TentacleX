package com.laxser.tentaclex.methods;

import java.util.Map.Entry;

import com.laxser.tentaclex.Method;

/**
 * 没有方法体的方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-3 下午04:36:57
 */
public abstract class TentacleBodilessMethod extends Method {

	/*public XoaBodilessMethod(String domain, int port, String path) {
		super(domain, port, path);
	}*/
	
	/**
	 * 将所有通过setParam方法加进来的参数都拼在path的querystring里，并返回。
	 * 
	 * 比如，原path为/blog/1234?type=1，通过setParam设置了两个参数a=1,b=2,
	 * 
	 * 则此方法应该返回/blog/1234?type=1&a=1&b=2
	 * 
	 * @return
	 */
	public String getPathWithFullQueryString() {
		
		//没有设置额外参数的情况
		if (this.parameters == null || this.parameters.size() == 0) {
			return path;
		}
		StringBuilder sb = new StringBuilder(path.length() + parameters.size() * 20);	//估算一个初始化长度减少re-allocate开销
		sb.append(path);
		if (path.indexOf('?') == -1) {	//没有'?'
			sb.append('?');
		} else {	//有'?'
			sb.append('&');
		}
		
		for (Entry<String, String[]> entry : parameters.entrySet()) {
			String name = entry.getKey();
			for (String value : entry.getValue()) {
				sb.append(urlEncode(name));
				sb.append('=');
				sb.append(urlEncode(value));
				sb.append('&');
			}
		}
		
		if (sb.length() > 0 || sb.charAt(sb.length() - 1) == '&') {	//remove trailing '&'
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	

	public static void main(String[] args) {
		System.out.println(TentacleMethodName.GET.toString());
	}
	
}
