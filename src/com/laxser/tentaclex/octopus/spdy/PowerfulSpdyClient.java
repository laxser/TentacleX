package com.laxser.tentaclex.octopus.spdy;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import com.laxser.tentaclex.commons.exception.TentacleConnectException;
import com.laxser.tentaclex.commons.netty.SpdyHttpRequest;
import com.laxser.tentaclex.commons.netty.SpdyHttpResponse;
import com.laxser.tentaclex.commons.spdy.datastructure.ExpireWheel;
import com.laxser.tentaclex.octopus.service.ResponseObserver;
import com.laxser.tentaclex.octopus.service.SpdyClient;
import com.laxser.tentaclex.octopus.spdy.netty.ChannelFactoryManager;
import com.laxser.tentaclex.octopus.spdy.netty.HashedWheelTimerManager;
import com.laxser.tentaclex.octopus.spdy.netty.TentacleClientPipelineFactory;

/**
 * 
 * 改良的Spdy client测试中
 * 
 * @author laxser  Date 2012-6-1 上午8:53:28
@contact [duqifan@gmail.com]
@PowerfulSpdyClient.java

 */
@ChannelPipelineCoverage("one")
public class PowerfulSpdyClient extends SimpleChannelUpstreamHandler implements SpdyClient {

	protected static final int[] fibonacci = new int[] { 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233};  //, 377, 610 };
	
	protected Log logger = LogFactory.getLog(this.getClass());
	
	private ClientBootstrap bootstrap;
    
	/**
	 * 延时重连时使用的计时器
	 */
	private final Timer reconnectTimer = HashedWheelTimerManager.getInstance().getTimer();
	
    private Channel channel = null;

	private ExpireWheel<ResponseObserver> obs = new ExpireWheel<ResponseObserver>(
			ExpireWheel.CAPACITY_2P14, 2);
	
	/**
	 * 连接是否有意被断开了。
	 * 
	 * 连接被断开可能有两种情况，一种是意外断开，比如网络故障或者远端节点宕机，
	 * 这种情况下应该自动延时重连。
	 * 
	 * 而如果上层程序主动断开连接，则不需要重连了。此时disconnectedIntentionally
	 * 应该为true
	 * 
	 */
	private volatile boolean disconnectedIntentionally = false;
	
    /**
     * +2自增的stream id
     */
    private AtomicInteger streamId = new AtomicInteger(1);

    /**
     * 重连次数
     */
    private int reconnectCount = 0;
    
    /**
     * stream id大过一定数值时需要重置回1，为了防止重复reset，需要上锁
     */
    private Object streamIdResetLock = new Object();
    
    /**
     * streamId的重置阈值，超过这个值就要进行reset了。
     * StreamId在SPDY协议规定里只有31个bit，也就是说省去了符号位，Integer.MAX_VALUE的二进制形式
     * 刚好是31个1，减去2048是为了缓冲一下，如果在重置回1之前有别的线程调用了getAndAdd，
     * 就越界了
     * 
     */
    private static final int STREAM_ID_RESET_THRESHOLE = Integer.MAX_VALUE - 2048;
    
    /**
     * 要连接的远程主机的地址
     */
    private InetSocketAddress remoteAddress = null;

    /**
     * 初始化时防止重入的锁
     */
    private ReentrantLock lock = new ReentrantLock();
    
    /**
     * 构造函数
     * @param inetSocketAddress 要连接的远程主机的地址
     */
    public PowerfulSpdyClient(InetSocketAddress inetSocketAddress) {
        this.remoteAddress = inetSocketAddress;
    }

    /**
     * 初始化，会建立到远程主机的连接。
     */
    public void init() {
        
        if (logger.isInfoEnabled()) {
            logger.info("Initiating SpdyClient connecting with: " + remoteAddress);
        }
        
    	if (lock.isLocked()) {
            return;
        }
    	lock.lock();
    	try {
			bootstrap = new ClientBootstrap(ChannelFactoryManager.getInstance()
					.getClientChannelFactory());
            // Configure the client.
            bootstrap.setOption("tcpNoDelay", true);
            bootstrap.setOption("keepAlive", true);
            
            // Set up the event pipeline factory.
            bootstrap.setPipelineFactory(new TentacleClientPipelineFactory(this));
            ChannelFuture future = bootstrap.connect(remoteAddress);
            channel = future.awaitUninterruptibly().getChannel();
            logChannelStatus("on init");
		} catch (Exception e) {
			throw new TentacleConnectException(
					"Error occurs while init connection to remote host:"
							+ getRemoteHost(), e);
		} finally {
			lock.unlock();
		}
    }

    public boolean isConnected() {
    	return channel != null && channel.isConnected();
    }
    

    @Override
    public void send(SpdyHttpRequest request, ResponseObserver ob) {
        
        if(!isConnected()) {
        	//logger.error("Not connected to node:" + address);
			throw new TentacleConnectException(
					"Client not connected to remote host:" + getRemoteHost());
        }
        
        int sid = streamId.getAndAdd(2);
        
        //StreamId在规定里只有31个bit，也就是说省去了符号位，Integer.MAX_VALUE的二进制形式
    	//刚好是31个1，减去2048是为了缓冲一下，如果在重置回1之前有别的线程调用了getAndAdd，
        //就越界了
        if (sid >= STREAM_ID_RESET_THRESHOLE) {	//DCL防重复reset
        	synchronized (streamIdResetLock) {
				if (streamId.get() >= STREAM_ID_RESET_THRESHOLE) {
					streamId.set(1);
				}
			}
        }
        request.setStreamId(sid);
        if (ob != null) obs.put(request.getStreamId(), ob);
        if (channel != null) {
            channel.write(request);
        } else {
			throw new TentacleConnectException(
					"Channel not available to remote host:" + getRemoteHost());
        }
    }

    public ChannelHandler getResponseChannelHandler() {
        return this;
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        logger.warn("channel disconnected: " + channel);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	if (logger.isInfoEnabled()) {
    		logger.info("connected to: " + this.remoteAddress + ", channel:" + ctx.getChannel());
    	}
    	reconnectCount = 0;	//链接成功后将reconnectCount重置为0
    	logChannelStatus("on channelConnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        
        /*
         * 有下面三种情况会进入channelClosed方法
         * 1. 服务器端断开了连接
         * 2. 重连失败，如遇到connection refused问题
         * 3. 客户端主动端口了连接
         * 
         * */
        if (this.disconnectedIntentionally) {   //主动断开的连接
            //do nothing
        } else {    //异常断开的情况下要重连
            logger.warn("channel closed:" + ctx.getChannel());
            delayedReconnect();
        }
        super.channelConnected(ctx, e);
    }
    
    /**
     * 延时重连
     */
    private void delayedReconnect() {
        int delay = calculateReconnectDelay(this.reconnectCount);
        if (logger.isInfoEnabled()) {
            logger.info("try reconnect after " + delay + " seconds, to: " + remoteAddress);
        }
        reconnectTimer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                reconnect();
            }
        }, delay, TimeUnit.SECONDS);
    }
    
    /**
     * 计算重连延迟，按fibonacci增长
     * 
     * @param retryCount
     * @return
     */
    private int calculateReconnectDelay(int retryCount) {
    	if (retryCount >= fibonacci.length) {
    		return fibonacci[fibonacci.length - 1];
    	} else {
    		return fibonacci[retryCount];
    	}
	}

	/**
     * 重连
     */
    private void reconnect() {
    	if (channel.isConnected()) {
			logger.warn("channel already connected:" + channel);
    		return;
    	}
    	reconnectCount ++;
    	if (logger.isInfoEnabled()) {
    		logger.info(reconnectCount + "-th reconnecting to:" + remoteAddress);
    	}
    	bootstrap.connect(remoteAddress).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				channel = channelFuture.getChannel();
				logChannelStatus("on reconnecting complete");
			}
		});
    }

    /**
     * log当前的连接状态信息
     * @param msg 说明信息
     */
    private void logChannelStatus(String msg) {
    	if (logger.isDebugEnabled()) {
    		if (channel == null) {
    			logger.debug(msg + ": channel null");
    		} else {
    			StringBuilder sb = new StringBuilder();
    			sb.append(msg + "-");
    			sb.append("channel:" + channel);
    			sb.append(", ");
        		sb.append("isBound:" + channel.isBound());
        		sb.append(", ");
        		sb.append("isOpen:" + channel.isOpen());
        		sb.append(", ");
        		sb.append("isConnected:" + channel.isConnected());
        		sb.append(", ");
        		sb.append("isReadable:" + channel.isReadable());
        		sb.append(", ");
        		sb.append("isWritable:" + channel.isWritable());
        		logger.debug(sb.toString());
    		}
    	}
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof SpdyHttpResponse) {
            SpdyHttpResponse shr = (SpdyHttpResponse) e.getMessage();
            ResponseObserver sro = obs.remove(shr.getStreamId());
            if (sro != null) {
                sro.messageReceived(shr);
            }
        } else {
        	//作为整个pipeline上的最后一个节点，代码不应该执行到这里
			logger.warn(this.getClass().getName() + " received message "
					+ e.getMessage().getClass().getName());
        	super.messageReceived(ctx, e);
        }
    }

    //有异常需要重新建立连接，每个client有自己的重建方法，这里简单地从容器里去掉
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    	logChannelStatus("on exceptionCaught");
    	Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
        	
        	/*
        	 * 这个逻辑是从例子里抄过来的，还没看出来会什么用。。。
        	 * 连接异常断掉的时候channel.close()会被ChannelFutureListener(line57)
        	 * 调用，也就是说，在exceptionCaught之前，channelClosed方法
        	 * 已经被调用过了，代码走到这里的时候channel已经被关掉了，
        	 * 这里重复关闭没有产生任何效果
        	 * 
        	 * */
        	ctx.getChannel().close();
        }
        
        /*
         * 这里不要加入重连代码，因为连接断掉的时候并不一定要抛出异常。
         * 而连接断掉的时候一定会进入channelClosed方法
         * 
         * */
        StringBuilder log = new StringBuilder(100);
        log.append("Error occurs while connecting with remote host:");
        log.append(getRemoteHost());
        if (cause instanceof java.net.ConnectException) {
            log.append(": ");
            log.append(cause.getMessage());
            logger.error(log.toString());
        } else {
            logger.error(log.toString(), cause);
        }
    }
    
    @Override
	public String getRemoteHost() {
		return remoteAddress.toString();
	}

    @Override
    public void disconnect() {
        if (logger.isInfoEnabled()) {
            logger.info("Trying to close connection to " + getRemoteHost());
        }
        
        //一定要在最前面设置disconnectedIntentionally为true，否则
        //调用channel.close()时channelClosed方法可能立即被触发，调用时需就不对了
        this.disconnectedIntentionally = true;
        
        if (!isConnected()) {
            logger.error("Connection already closed: " + getRemoteHost());
            return;
        }
        
        ChannelFuture future = channel.close();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture cf) throws Exception {
                
                if (cf.isSuccess()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Successfully disconnected with remote host:" + remoteAddress);
                    }
                } else {
                    Throwable cause = cf.getCause();
                    if (cause != null) {
                        logger.error("Fail to disconnected with remote host:" + remoteAddress, cause);
                    } else {
                        logger.error("Fail to disconnected with remote host:" + remoteAddress);
                    }
                }
            }
        });
    }

}
