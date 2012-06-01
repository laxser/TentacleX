package com.laxser.tentaclex.octopus.spdy;

import java.net.InetSocketAddress;

/**
 * 
 * @author laxser
 * @ contact duqifan@gmail.com
 * TentacleX 计划
 * date: 2012-6-1
 * time 上午8:53:34
 */
public class Main {

    public static void main(String[] args) {
        
        InetSocketAddress inetSocketAddress = new InetSocketAddress("10.22.200.140", 8188);
        PowerfulSpdyClient client = new PowerfulSpdyClient(inetSocketAddress);
        client.init();
        
        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        client.disconnect();
        
        /*mgr.getClient(hostport);
        System.out.println();
        mgr.destroyClient(hostport);
        System.out.println();
        mgr.getClient(hostport);
        System.out.println();
        mgr.destroyClient(hostport);*/
    }
}
