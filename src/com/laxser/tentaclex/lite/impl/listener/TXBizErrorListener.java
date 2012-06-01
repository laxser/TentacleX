package com.laxser.tentaclex.lite.impl.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.laxser.tentaclex.StatusNotOkException;
import com.laxser.tentaclex.TentacleResponse;
import com.laxser.tentaclex.commons.bean.TXBizErrorBean;
import com.laxser.tentaclex.lite.ServiceFuture;

/**
 * 
 * 监听返回，如果返回的是XoaBizErrorBean，则提取之；
 * 如果是其它错误，则log之；
 * 如果是成功的response，则什么也不做。
 * 
 * @author laxser  Date 2012-5-31 下午4:47:54
@contact [duqifan@gmail.com]
@TXBizErrorListener.java

 */
public class TXBizErrorListener extends ServiceFailureListener<TXBizErrorBean, RuntimeException> {

	protected static final Log default_logger = LogFactory.getLog(TXBizErrorListener.class);
	
	private final Log myLogger;
	
	public TXBizErrorListener() {
	    this.myLogger = default_logger;
	}
	
	public TXBizErrorListener(Log customLogger) {
	    this.myLogger = customLogger;
	}
	
	
	@Override
	public TXBizErrorBean onError(ServiceFuture<?> future) throws RuntimeException {
		Throwable cause = future.getCause();
		if (cause instanceof StatusNotOkException) {
			StatusNotOkException e = (StatusNotOkException)cause;
			TentacleResponse response = e.getResponse();
			int code = response.getStatusCode();
			if (code == TXBizErrorBean.STATUS_CODE) {
				TXBizErrorBean error = response.getContentAs(TXBizErrorBean.class);
				return error;
			} else {
			    myLogger.error("Status code: " + code , cause);
			}
		} else {
		    myLogger.error("", cause);
		}
		return null;
	}
}
