package com.laxser.tentaclex.lite.throughput;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-7-14 下午06:43:01
 */
public class ThroughputCounter {

    private Map<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();
    
    public int incrAndGet(String key) {
        AtomicInteger tp = map.get(key);
        if (tp == null) {
            synchronized (key.intern()) {
                tp = map.get(key);
                if (tp == null) {
                    tp = new AtomicInteger();
                    map.put(key, tp);
                }
            }
        }
        return tp.incrementAndGet();
    }
    
    public int decrAndGet(String key) {
        AtomicInteger tp = map.get(key);
        if (tp.get() == 0) {
            throw new IllegalArgumentException("Key not exists: " + key);
        }
        return tp.decrementAndGet();
    }
    
}
