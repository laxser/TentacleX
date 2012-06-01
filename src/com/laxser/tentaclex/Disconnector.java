package com.laxser.tentaclex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import com.laxser.tentaclex.octopus.service.SpdyClientManager;
import com.laxser.tentaclex.octopus.spdy.netty.HashedWheelTimerManager;
import com.laxser.tentaclex.registry.TentacleRegistry;
import com.laxser.tentaclex.registry.TentacleRegistryListener;
import com.laxser.tentaclex.registry.TentacleServiceDescriptor;

/**
 * 负责在某个服务节点充registry中删除或者去掉的时候，断开与其服务器的连接
 * 也就是把触手和章鱼都干掉
 * 
 * @author laxser  Date 2012-6-1 上午8:44:52
@contact [duqifan@gmail.com]
@Disconnector.java

 */
public class Disconnector implements TentacleRegistryListener {

    /**
     * 延迟多长时间后再disconnect，防止disconnect的时候有的请求还没有结束
     */
    private static final int DISCONNECT_DELAY = 5;
    
    private static Log logger = LogFactory.getLog(Disconnector.class);
    
    private final TentacleRegistry registry;
    
    private HashedWheelTimer timer = HashedWheelTimerManager.getInstance().getTimer();
    
    public Disconnector(TentacleRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public void onNodeDeleted(TentacleServiceDescriptor node) {
        delayedDisconnect(node);
    }

    @Override
    public void onNodeDisabled(TentacleServiceDescriptor node) {
        delayedDisconnect(node);
    }
    
    private void delayedDisconnect(final TentacleServiceDescriptor node) {
        timer.newTimeout(new TimerTask() {
            
            @Override
            public void run(Timeout timeout) throws Exception {
                disconnect(node);
            }
        }, DISCONNECT_DELAY, TimeUnit.SECONDS);
    }
    
    /**
     * 完成与指定节点断开连接的操作
     * @param node
     */
    private void disconnect(TentacleServiceDescriptor node) {
        
        //从registry查找看该节点还在被那些服务使用
        List<String> serviceIds = registry.lookup(node.getIpAddress(), node.getPort());
        
        List<String> otherServiceIds = new ArrayList<String>();
        
        boolean usedByOtherService = false;
        
        //判断是否还在被其他服务使用
        for (String serviceId : serviceIds) {
            if (!serviceId.equals(node.getServiceId())) {
                otherServiceIds.add(serviceId);
                usedByOtherService = true;
            }
        }
        
        if (usedByOtherService) {   //还在被别的服务使用
            if (logger.isInfoEnabled()) {   //log出具体使用情况
                StringBuilder sb = new StringBuilder(100);
                sb.append("Do NOT disconnect with ");
                sb.append(node.getIpAddress());
                sb.append(":");
                sb.append(node.getPort());
                sb.append(", still using by ");
                sb.append(otherServiceIds.size());
                sb.append(" other services: ");
                for (String serviceId : otherServiceIds) {
                    sb.append(serviceId);
                    sb.append(",");
                }
                if (sb.charAt(sb.length() - 1) == ',') {
                    sb.setLength(sb.length() - 1);
                }
                logger.info(sb.toString());
            }
        } else {    //不再被别的服务使用了
            
            if (logger.isInfoEnabled()) {
                StringBuilder sb = new StringBuilder(100);
                sb.append("Node not using by any service anymore, try to disconnect it: ");
                sb.append(node.getIpAddress());
                sb.append(":");
                sb.append(node.getPort());
                logger.info(sb.toString());
            }
                    
            //销毁掉client
            String hostport = node.getIpAddress() + ":" + node.getPort();
            SpdyClientManager.getInstance().destroyClient(hostport);
        }
    }
}
