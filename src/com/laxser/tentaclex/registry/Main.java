package com.laxser.tentaclex.registry;

import com.laxser.tentaclex.registry.impl.TentacleRegistryLogListener;
import com.laxser.tentaclex.registry.impl.zookeeper.ZookeeperBasedRegistry;


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
