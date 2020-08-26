package com.qipeng.qrpc.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author qipenglin
 **/
public class NetUtil {
    public static String getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
