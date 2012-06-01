package com.laxser.tentaclex.octopus.service;

import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;

/**
 *@author laxser  Date 2012-6-1 上午8:54:00
@contact [duqifan@gmail.com]
@ResponseObserver.java
 * 
 */
public interface ResponseObserver {

    public void messageReceived(SpdyHttpResponse response);
}
