package com.laxser.tentaclex.commons.thread;

import java.util.concurrent.ThreadFactory;

/**
 * 给netty的nio线程使用的ThreadFactory，主要是为了设置 线程的优先级
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-4-18 上午10:39:50
 */
public class NioThreadFactory implements ThreadFactory {

    final ThreadGroup group;

    public NioThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r);
        t.setDaemon(true);
        t.setPriority(Thread.MAX_PRIORITY);
        return t;
    }

}
