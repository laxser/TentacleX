package com.laxser.tentaclex.lite;

import java.util.EventListener;


/**
 * {@link ServiceFuture}异步回调的监听器
 * 
 * @author laxser  Date 2012-6-1 上午8:55:21
@contact [duqifan@gmail.com]
@ServiceFutureListener.java

 */
public interface ServiceFutureListener extends EventListener {

	void operationComplete(ServiceFuture<?> future) throws Exception;
}
