package com.laxser.tentaclex.registry.checkin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:53:15
 */
public abstract class AbstractCheckinService implements ClientCheckinService {
    
    private static final ExecutorService exe = Executors.newFixedThreadPool(1);
    
    /**
     * HashSet是非线程安全的，但是程序保证了对checkinSet进行修的修改
     * 操作都是在一个线程里面完成的，所以就不存在线程安全问题了
     */
    private Set<String> checkinSet = new HashSet<String>();

    @Override
    public void checkin(final String clientIp, final String serviceId) {
        
        final String checkinId = genCheckinId(clientIp, serviceId); 
        if(checkinSet.contains(checkinId)) {
            return;
        } else {
            
            //异步checkin
            exe.submit(new Runnable() {
                @Override
                public void run() {
                    if (storeCheckin(clientIp, serviceId)) {   //checkin成功就记录在本地
                        checkinSet.add(checkinId);
                    }
                }
            });
        }
    }

    public abstract boolean storeCheckin(String clientIp, String serviceId);
    
    private String genCheckinId(String clientIp, String serviceId) {
        StringBuilder sb = new StringBuilder(clientIp.length() + serviceId.length() + 1);
        sb.append(serviceId);
        sb.append('-');
        sb.append(clientIp);
        return sb.toString();
    }
    
}
