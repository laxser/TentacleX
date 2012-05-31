package com.laxser.tentaclex.lite;

import java.util.EventListener;


/**
 * {@link ServiceFuture}异步回调的监听器
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-11-18 下午06:30:54
 */
public interface ServiceFutureListener extends EventListener {

	void operationComplete(ServiceFuture<?> future) throws Exception;
}
