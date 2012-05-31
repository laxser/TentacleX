package com.laxser.tentaclex.registry.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-5-4 下午12:40:48
 */
public class IPUtil {

    public static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "UnknownHost";
        }
    }
    
}
