package com.laxser.tentaclex.registry;

import com.laxser.tentaclex.registry.impl.TentacleRegistryLogListener;
import com.laxser.tentaclex.registry.impl.zookeeper.ZookeeperBasedRegistry;

/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:46:36
 */
public class Main {
    
    public static void main(String[] args) {
        
        ZookeeperBasedRegistry reg = new ZookeeperBasedRegistry();
        reg.init();
        
        reg.queryService("test.xoa.renren.com");
        
        reg.addListener(new TentacleRegistryLogListener());
        
        synchronized (reg) {
            try {
                reg.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
