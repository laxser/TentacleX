package com.laxser.tentaclex.octopus.service;

import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;

/**
 * @author Lookis (lookisliu@gmail.com)
 * 
 */
public interface ResponseObserver {

    public void messageReceived(SpdyHttpResponse response);
}
