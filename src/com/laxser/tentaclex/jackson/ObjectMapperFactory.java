package com.laxser.tentaclex.jackson;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 由于{@link ObjectMapper}是带缓存的，所以通过{@link ObjectMapperFactory} 来获取
 * {@link ObjectMapper}对象能保证系统中有唯一的实例，提高性能
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-26 上午11:35:58
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
		mapper.getDeserializationConfig().disable(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
}
