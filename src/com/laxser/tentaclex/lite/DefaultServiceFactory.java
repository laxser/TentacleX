package com.laxser.tentaclex.lite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.laxser.tentaclex.Tentacle;
import com.laxser.tentaclex.lite.annotation.TXService;
import com.laxser.tentaclex.lite.definition.ServiceDefinition;
import com.laxser.tentaclex.util.ClassUtils;


/**
 * {@link ServiceFactory}的默认实现
 * 
 * @author laxser  Date 2012-6-1 上午8:55:57
@contact [duqifan@gmail.com]
@DefaultServiceFactory.java

 */
public class DefaultServiceFactory implements ServiceFactory {
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
	private Map<Class<?>, ServiceDefinition> xoaServices = new ConcurrentHashMap<Class<?>, ServiceDefinition>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceInterface, Tentacle client) {
		
		ServiceDefinition servicdDef = xoaServices.get(serviceInterface);
		
		if (servicdDef == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Definition NOT found for " + serviceInterface.getName());
			}
			for (Annotation annotation : serviceInterface.getAnnotations()) {
				if (annotation instanceof TXService) {
					TXService xoaService = (TXService)annotation;
					servicdDef = new ServiceDefinition();
					servicdDef.setServiceId(xoaService.serviceId());
					//servicdDef.setHosts(xoaService.hosts());
					xoaServices.put(serviceInterface, servicdDef);
					break;
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Definition found for " + serviceInterface.getName());
				}
			}
		}
		
		if (servicdDef == null) {
			throw new IllegalArgumentException(serviceInterface + " must be annotated with " + TXService.class);
		}

		ServiceInvocationHandler handler;
		if (client == null) {
			handler = new ServiceInvocationHandler(servicdDef);
		} else {
			handler = new ServiceInvocationHandler(servicdDef, client);
		}
		
		T proxy = (T)Proxy.newProxyInstance(
				ClassUtils.getDefaultClassLoader(), 
				new Class<?>[]{serviceInterface}, handler);
		return proxy;
	}

	@Override
	public <T> T getService(Class<T> serviceInterface) {
		return getService(serviceInterface, null);
	}

}
