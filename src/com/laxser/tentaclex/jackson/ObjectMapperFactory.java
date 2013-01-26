package com.laxser.tentaclex.jackson;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 由于{@link ObjectMapper}是带缓存的，所以通过{@link ObjectMapperFactory} 来获取
 * {@link ObjectMapper}对象能保证系统中有唯一的实例，提高性能
 * 
 * @author laxser  Date 2012-6-1 上午8:57:30
@contact [duqifan@gmail.com]
@ObjectMapperFactory.java

 */
public class ObjectMapperFactory {

	private static ObjectMapperFactory instance = new ObjectMapperFactory();

	private ObjectMapper mapper;

	public static ObjectMapperFactory getInstance() {
		return instance;
	}

	private ObjectMapperFactory() {
		initObjectMapper();
	}

	public ObjectMapper getObjectMapper() {
		return mapper;
	}

	private void initObjectMapper() {
		mapper = new ObjectMapper();
		//disable掉FAIL_ON_UNKNOWN_PROPERTIES属性，增强容错性
		   mapper.disable(
	                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    }
}
