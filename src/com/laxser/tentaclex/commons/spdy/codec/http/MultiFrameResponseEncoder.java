package com.laxser.tentaclex.commons.spdy.codec.http;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;
import com.laxser.tentaclex.commons.spdy.frame.ControlFrame;
import com.laxser.tentaclex.commons.spdy.frame.DataFrame;
import com.laxser.tentaclex.commons.spdy.frame.FrameFactory;
import com.laxser.tentaclex.commons.spdy.frame.control.SynReply;

/**
 * 将SpdyHttpResponse的body拆分为多个DataFrame进行发送
 * 
 * @author 李伟博, 刘敬思
 * @since 2010-4-11 下午05:06:29
 */
@ChannelPipelineCoverage("one")
public class MultiFrameResponseEncoder extends SimpleChannelDownstreamHandler {

	protected Log logger = LogFactory.getLog(this.getClass());

	private FrameFactory frameFactory = FrameFactory.getInstance();
	
	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof SpdyHttpResponse) {
			SpdyHttpResponse responseToSend = (SpdyHttpResponse) e.getMessage();
			
			/*
			 * 四种情况：
			 * 1. (!committed && !finished)发SynReply,body有数据就发DataFrame，否则不发
			 * 2. (!committed && finished)如果无body，SynReply带FIN_FLAG，不发DataFrame；
			 *     如果有body，SynReply不带FIN_FLAG， 发DataFrame带FIN_FLAG
			 * 3. (committed && !finished)body有数据就发DataFrame，否则不发
			 * 4. (committed && finished)有无数据都要发一个DataFrame，带FIN_FLAG
			 * */
			boolean committed = responseToSend.isCommited();
			boolean finished = responseToSend.isFinished();
			
			if (!committed && !finished) {
				sendSynReply(responseToSend, false, ctx, e);
				if (responseToSend.getContent().readableBytes() > 0) {
					sendDataFrame(responseToSend, false, ctx, e);
				}
			} else if (!committed && finished) {
				if (responseToSend.getContent().readableBytes() == 0) {
					sendSynReply(responseToSend, true, ctx, e);
				} else {
					sendSynReply(responseToSend, false, ctx, e);
					sendDataFrame(responseToSend, true, ctx, e);
				}
			} else if (committed && !finished) {
				if (responseToSend.getContent().readableBytes() > 0) {
					sendDataFrame(responseToSend, false, ctx, e);
				}
			} else if (committed && finished) {
				sendDataFrame(responseToSend, true, ctx, e);
			} else {
				logger.error("code should never reach here!!!");
			}
		} else {
			super.writeRequested(ctx, e);
		}
	}

	private void sendSynReply(SpdyHttpResponse responseToSend, boolean withFinFlag, 
			ChannelHandlerContext ctx, MessageEvent e) {
		int streamId = responseToSend.getStreamId();

		SynReply responseHeader = null;
		byte flags = 0;
		//状态已经被设置为finished且没有BODY的请求，只发送一个SynStream数据
		//之所以还要判断一下body是因为一个response可能是第一次commit同时已经finish了
		if (withFinFlag) { 
			flags = ControlFrame.FLAG_FIN;
		}
		responseHeader = frameFactory.newSynReply(flags, streamId);
		Set<String> headerNames = responseToSend.getHeaderNames();

		HttpVersion version = responseToSend.getProtocolVersion();
		responseHeader.putNV("version", version.getText());
		responseHeader.putNV("status", Integer.toString(responseToSend
				.getStatus().getCode()));
		for (String name : headerNames) {
			responseHeader.putNV(name, responseToSend.getHeader(name));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("send SynReply for stream:" + streamId
					+ (flags == ControlFrame.FLAG_FIN ? " with FIN_FLAG" : ""));
		}
		
		// 先发送Header
		Channels
				.write(ctx, e.getFuture(), responseHeader, e.getRemoteAddress());
	}

	private void sendDataFrame(SpdyHttpResponse responseToSend, boolean withFinFlag,
			ChannelHandlerContext ctx, MessageEvent e) {
		ChannelBuffer content = responseToSend.getContent();
		int streamId = responseToSend.getStreamId();
		byte flags = 0;
		if (withFinFlag) {
			flags = ControlFrame.FLAG_FIN;
		}
		// copy data from content
		byte[] data = new byte[content.readableBytes()];
		
		content.readBytes(data);
		content.clear();	//reset readIndex and writeIndex
		//DataFrame dataFrame = new DefaultDataFrame(streamId, flags, data);
		DataFrame dataFrame = FrameFactory.getInstance().newDataFrame(streamId, flags, data);
		Channels.write(ctx, e.getFuture(), dataFrame, e.getRemoteAddress());
		if (logger.isDebugEnabled()) {
			logger.debug("send DataFrame for stream:" + streamId + ", "
					+ data.length + "bytes"
					+ (flags == ControlFrame.FLAG_FIN ? " with  FIN_FLAG" : ""));
		}
	}

	public static void main(String[] args) {
		byte flags = 0;
		flags = (byte) (flags | ControlFrame.FLAG_FIN);
		System.out.println(flags);

		flags = ControlFrame.FLAG_FIN;
		System.out.println(flags);
	}

}
