package com.laxser.tentaclex.commons.netty;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * Represents the http response wrapped by a SPDY stream.
 * 
*@author laxser  Date 2012-5-31 下午4:57:14
@contact [duqifan@gmail.com]
@SpdyHttpResponse.java

 */
public class SpdyHttpResponse extends DefaultHttpResponse {

    /**
     * the stream-ID of the associated SPDY stream
     */
    private int streamId;

    private boolean commited;
    
    private boolean finished;
    
    /**
     * Creates a new instance.
     * 
     * @param version the HTTP version of this response
     * @param status the status of this response
     * @param streamId the stream-ID of the associated SPDY stream
     */
    public SpdyHttpResponse(HttpVersion version, HttpResponseStatus status, int streamId) {
        super(version, status);
        setStreamId(streamId);
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

	public boolean isCommited() {
		return commited;
	}

	public void setCommited(boolean commited) {
		this.commited = commited;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
    
}
