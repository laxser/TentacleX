package com.laxser.tentaclex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.bean.TXBizErrorBean;
import com.laxser.tentaclex.commons.exception.TentacleXException;
import com.laxser.tentaclex.commons.netty.SpdyHttpRequest;
import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;
import com.laxser.tentaclex.methods.TentacleDeleteMethod;
import com.laxser.tentaclex.methods.TentacleEntityEnclosingMethod;
import com.laxser.tentaclex.methods.TentacleGetMethod;
import com.laxser.tentaclex.methods.TentacleMethodName;
import com.laxser.tentaclex.methods.TentacleMultiFormatPostMethod;
import com.laxser.tentaclex.methods.TentaclePutMethod;
import com.laxser.tentaclex.octopus.service.ResponseObserver;
import com.laxser.tentaclex.octopus.service.SpdyClient;
import com.laxser.tentaclex.octopus.service.SpdyClientManager;
import com.laxser.tentaclex.registry.TentacleRegistry;
import com.laxser.tentaclex.registry.TentacleRegistryFactory;
import com.laxser.tentaclex.registry.TentacleServiceDescriptor;
import com.laxser.tentaclex.registry.impl.TentacleRegistryLogListener;
import com.laxser.tentaclex.response.DefaultTentacleResponse;



/**
 * TentacleX 服务的客户端，非常形象。Octopus发出一条条的Tentacle，作为一个客户端，Octopus的每条方法都是线程安全的。
 * 因此“触手”用完了不要扔掉，尽量复用
 * 
 * 
 * 
 *@author laxser  Date 2012-5-31 下午4:48:04
@contact [duqifan@gmail.com]
@Octopus.java

 */
public class Octopus implements Tentacle {

    public static final String VERSION = "TentacleX-Client/1.0";
    
	public static final String PROTOCOL_HTTP = "http";

	public static final String PROTOCOL_SPDY = "spdy";

	private static final long DEFUALT_TIMEOUT = 2000;
	
	protected static final Log logger = LogFactory.getLog(Octopus.class);
	
	private TentacleRegistry registry;
	
	public Octopus(){
		this(TentacleRegistryFactory.getInstance().getDefaultRegistry());
	}
	
	public Octopus(TentacleRegistry registry) {
		this.registry = registry;
		registry.addListener(new TentacleRegistryLogListener());
        registry.addListener(new Disconnector(registry));
	}
	
	/**
	 * 执行指定的xoa方法
	 * 
	 * @param method
	 *            要执行的方法
	 * @return {@link Future}包含XoaResonse对象
	 * @throws IOException
	 * @throws TentacleXException
	 */
	public Future<TentacleResponse> submit(Method method) {
		return submitMethodSpdy(method);
	}
	
	public TentacleResponse execute(Method method) {
		return execute(method, DEFUALT_TIMEOUT);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public TentacleResponse execute(Method method, long timeout) {
	    long startTime = System.currentTimeMillis();
		Future<TentacleResponse> future = submit(method);
		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
		    long endTime = System.currentTimeMillis();
		    if (future instanceof OperationFuture) {
		      
				OperationFuture of = (OperationFuture) future;
		        InvocationInfo info = of.invocationInfo;
		        StringBuilder sb = new StringBuilder();
	            sb.append("Tentacle timeout in ");
	            sb.append(endTime - startTime);
	            sb.append(" ms: ");
	            sb.append(info.getMethodName());
	            sb.append(" ");
	            sb.append(info.getUrl());
	            sb.append(", remote host: ");
	            sb.append(info.getRemoteHost());
	            throw new RuntimeException(sb.toString(), e);
		    } else {
		        //this should never happen
		        throw new RuntimeException("Timed out waiting for operation", e);
		    }
		}
	}
	
	/**
	 * ping一个服务，看有多少服务节点能够正常相应
	 * @param serviceId 服务id
	 * @return 能够正常相应的节点个数
	 */
	public int ping(String serviceId) {
		
	    int ret = 0;
	    
	    List<TentacleServiceDescriptor> nodes = registry.queryServices(serviceId);
		
	    if (nodes == null) {
	        return ret;
	    }
	    
		for (TentacleServiceDescriptor desc : nodes) {
			String realHost = desc.getIpAddress() + ":" + desc.getPort();
			SpdyClient client = SpdyClientManager.getInstance().getClient(realHost);
			if (client.isConnected()) {
				ret ++;
			}
		}
		return ret;
	}
	
	public InvocationInfo submit(Method xoaMethod, final TentacleResponseCallback callback) {
		
		DefaultInvocationInfo info = new DefaultInvocationInfo();
		info.setMethodName(xoaMethod.getName().toString());
		info.setUrl(xoaMethod.getUrl());
		
		String serviceId = xoaMethod.getServiceId();
		TentacleServiceDescriptor desc = registry.queryService(serviceId);
		if (desc == null) {
			throw new ServiceNotFoundException(serviceId);
		}
		
		int port = desc.getPort();
		
		//如果xoaMethod中指定了端口，就覆盖掉registry中配置的
		if (xoaMethod.getPort() != -1) {
			port = xoaMethod.getPort();
		}
		
		//hostname + port就是host
		
		//virtual host
		String virtualHost = desc.getServiceId() + ":" + port;
		
		//real host
		String realHost = desc.getIpAddress() + ":" + port;

		SpdyClient client = SpdyClientManager.getInstance().getClient(realHost);
		if (!client.isConnected()) {
			logger.warn("not connect to remote host:" + realHost);
			List<TentacleServiceDescriptor> descs = registry.queryServices(serviceId);
			if (descs == null || descs.size() == 0) {
				throw new ServiceNotFoundException(serviceId);
			}
			List<String> realHosts = new ArrayList<String>(descs.size() - 1);
			for (TentacleServiceDescriptor tempDesc : descs) {
				if (tempDesc != desc) {
					//自定义端口在这种情况下不支持
					realHosts.add(tempDesc.getIpAddress() + ":" + tempDesc.getPort());
				}
			}
			//从指定的那些real hosts中找到一个可用的
			client = SpdyClientManager.getInstance().findHealthyClient(realHosts);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Find a healthy node of " + serviceId + ": " + client.getRemoteHost());
		}
		
		SpdyHttpRequest shr = wrapSpdyHttpRequest(xoaMethod, virtualHost);
		final String healthyRealHost = client.getRemoteHost();
		
		info.setRemoteHost(healthyRealHost);
		
		client.send(shr, new ResponseObserver() {
			@Override
			public void messageReceived(SpdyHttpResponse response) {
				DefaultTentacleResponse xoaResponse = new DefaultTentacleResponse();
				xoaResponse.setRemoteHost(healthyRealHost);
				xoaResponse.setStatusCode(response.getStatus().getCode());
				ChannelBuffer buff = response.getContent();
				int length = buff.readableBytes();
				byte[] content = new byte[length];
				buff.readBytes(content);
				xoaResponse.setContent(content);
				for (String headerName : response.getHeaderNames()) {
					xoaResponse.setHeader(headerName, response.getHeader(headerName));
				}
				callback.responseReceived(xoaResponse);
			}
		});
		return info;
	}
	
	private Future<TentacleResponse> submitMethodSpdy(Method xoaMethod) {
		final CountDownLatch latch = new CountDownLatch(1);
		final OperationFuture<TentacleResponse> of = new OperationFuture<TentacleResponse>(
				xoaMethod.getServiceId(), latch, DEFUALT_TIMEOUT);
		InvocationInfo info = submit(xoaMethod, new TentacleResponseCallback() {
			@Override
			public void responseReceived(TentacleResponse response) {
				of.set(response);
				latch.countDown();
			}
		});
		of.invocationInfo = info;
		return of;
	}
	
	public static interface TentacleResponseCallback {
		public void responseReceived(TentacleResponse response);
	}
	
	/**
	 * 将一个XoaMethod转换为SpdyHttpRequest
	 * 
	 * @param xoaMethod
	 * @return
	 */
	private SpdyHttpRequest wrapSpdyHttpRequest(Method xoaMethod, String host) {
		
		SpdyHttpRequest shr;
		if (xoaMethod.getName() == TentacleMethodName.GET) {
			
			TentacleGetMethod getMethod = (TentacleGetMethod)xoaMethod;
			shr = new SpdyHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
					getMethod.getPathWithFullQueryString());
		} else if (xoaMethod.getName() == TentacleMethodName.DELETE){
			
			TentacleDeleteMethod deleteMethod = (TentacleDeleteMethod)xoaMethod;
			shr = new SpdyHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE,
					deleteMethod.getPathWithFullQueryString());
		
		} else if (xoaMethod.getName() == TentacleMethodName.PUT){
			TentaclePutMethod putMethod = (TentaclePutMethod)xoaMethod;
			shr = new SpdyHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT,
					putMethod.getPathWithFullQueryString());
		} else if (xoaMethod instanceof TentacleEntityEnclosingMethod) {
			TentacleEntityEnclosingMethod eeMethod = (TentacleEntityEnclosingMethod)xoaMethod;
			if (xoaMethod.getName() == TentacleMethodName.POST) {
				shr = new SpdyHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
						eeMethod.getPath());
			} /*else if(xoaMethod.getName() == XoaMethodName.PUT) {
				shr = new SpdyHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT,
						eeMethod.getPath());
			}*/ else {
				throw new RuntimeException("Unsupported method:" + xoaMethod.getName());
			}
			
			byte[] body = eeMethod.getBody();
			
			//设置Content-Length非常重要，否则服务器端不知道请求何时结束
			shr.setHeader(HttpHeaders.Names.CONTENT_LENGTH, Integer.toString(body.length));
			
			ChannelBuffer content = ChannelBuffers.wrappedBuffer(body);
			shr.setContent(content);
			
			if (xoaMethod instanceof TentacleMultiFormatPostMethod) {
			    shr.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/xoa-multiformat");
			} else {
			    //有body的request才用Content-Type的header
	            shr.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
			}
			
			
		} else {
			throw new RuntimeException("Unsupported method:" + xoaMethod.getName());
		}
		
		shr.setHeader(HttpHeaders.Names.HOST, host);
		shr.setHeader(HttpHeaders.Names.ACCEPT, Method
						.buildAcceptHeaderValue(new String[] {
								Method.ACCEPT_TEXT_PLAIN,
								Method.ACCEPT_SERIALIZABLE,
								Method.ACCEPT_PROTOC_BUFF,
								Method.ACCEPT_JSON}));

		/*shr.addHeader(HttpHeaders.Names.ACCEPT_ENCODING, 
				HttpHeaders.Values.GZIP + ", " + HttpHeaders.Values.DEFLATE);*/
		shr.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.DEFLATE);
		shr.setHeader(HttpHeaders.Names.ACCEPT_CHARSET, "utf-8");
		shr.setHeader(HttpHeaders.Names.USER_AGENT, VERSION);
		
		//copy headers
		Map<String, String> headers = xoaMethod.getHeaders();
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				shr.setHeader(entry.getKey(), entry.getValue());
			}
		}
		return shr;
	}
	

	/**
	 * 异步调用的Future
	 * @param <V>
	 */
	private static class OperationFuture<V> implements Future<V> {

		private final CountDownLatch latch;
		
		private final String serviceId;
		
		private V v;
		
		private final long defaultTimeout;
		
		private InvocationInfo invocationInfo;
		
		public OperationFuture(String serviceId, CountDownLatch latch, long defaultTimeout) {
			this.serviceId = serviceId;
			this.latch = latch;
			this.defaultTimeout = defaultTimeout;
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public void set(V v) {
			this.v = v;
		}
		
		@Override
		public V get() throws InterruptedException, ExecutionException {
			try {
				return get(defaultTimeout, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				throw new RuntimeException("Timed out waiting for operation", e);
			}
		}
		
		@Override
		public V get(long timeout, TimeUnit units)
				throws InterruptedException, TimeoutException,
				ExecutionException {
			if (!latch.await(timeout, units)) {
				throw new TimeoutException("Timed out waiting for operation");
			}
			
			if (isCancelled()) {
				throw new ExecutionException(new RuntimeException("Cancelled"));
			}
			
			if (v instanceof TentacleResponse) {
				TentacleResponse response = (TentacleResponse) v;
				if (response.getStatusCode() != 200) {
				    
				    StringBuilder msg = new StringBuilder(100);
				    msg.append("[");
				    msg.append(this.serviceId);
				    msg.append("] Status code ");
				    msg.append(response.getStatusCode());
				    msg.append(" from remote host ");
				    msg.append(response.getRemoteHost());
				    
				    if (response.getStatusCode() == TXBizErrorBean.STATUS_CODE) {
				        
				        TXBizErrorBean errorBean = response.getContentAs(TXBizErrorBean.class);
				        msg.append(", errorCode: ");
				        msg.append(errorBean.getErrorCode());
				        msg.append(", errorMsg: ");
				        msg.append(errorBean.getMessage());
				    }
					throw new StatusNotOkException(msg.toString()).setResponse(response);
				}
			}
			return v;
		}
		
		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return latch.getCount() == 0;
		}
	}
}
