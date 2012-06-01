package com.laxser.tentaclex.commons.netty;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * Represents the http request wrapped by a SPDY stream.
 * 
 *@author laxser  Date 2012-5-31 下午4:56:52
@contact [duqifan@gmail.com]
@SpdyHttpRequest.java
*/
public class SpdyHttpRequest extends DefaultHttpRequest {

	/**
	 * the stream-ID of the associated SPDY stream
	 */
	private int streamId;
	
	/**
     * Creates a new instance.
     *
     * @param httpVersion the HTTP version of the request
     * @param method      the HTTP method of the request
     * @param uri         the URI or path of the request
     * @param streamId    the stream-ID of the associated SPDY stream
     */
    public SpdyHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, int streamId) {
    	super(httpVersion, method, uri);
    	this.streamId = streamId;
    }
    
    public SpdyHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
    	this(httpVersion, method, uri, 0);
    }

	public int getStreamId() {
		return streamId;
	}

	public void setStreamId(int streamId) {
		this.streamId = streamId;
	}
}
