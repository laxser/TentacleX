package com.laxser.tentaclex.http;


/**
 * @author laxser  Date 2012-5-31 下午4:49:19
@contact [duqifan@gmail.com]
@HttpClientFactory.java

 */
public class HttpClientFactory {

	private static HttpClientFactory instance = new HttpClientFactory();
	
	public static HttpClientFactory getInstance() {
		return instance;
	}
	
	/*private HttpClient client = null;
	
	private HttpClientFactory() {
		try {
			MultiThreadedHttpConnectionManager httpManager = new MultiThreadedHttpConnectionManager();
			httpManager.getParams().setDefaultMaxConnectionsPerHost(250);
			httpManager.getParams().setConnectionTimeout(1000);
			httpManager.getParams().setSoTimeout(1000);
			httpManager.getParams().setMaxTotalConnections(256);
			httpManager.getParams().setTcpNoDelay(true);
			client = new HttpClient(httpManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HttpClient getHttpClient() {
		return client;
	}
	*/
}
