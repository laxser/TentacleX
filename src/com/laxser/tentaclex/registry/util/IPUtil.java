package com.laxser.tentaclex.registry.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author laxser  Date 2012-6-1 上午9:34:10
@contact [duqifan@gmail.com]
@IPUtil.java

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
