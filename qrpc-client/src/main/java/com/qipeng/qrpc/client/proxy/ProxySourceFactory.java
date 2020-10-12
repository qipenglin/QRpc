package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.config.RpcConfig;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/10/12 12:09 下午
 */
public class ProxySourceFactory {

    private volatile static ProxySource instance;

    static ProxySource getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ProxySourceFactory.class) {
            if (instance != null) {
                return instance;
            }
            switch (RpcConfig.PROXY) {
                case "jdk":
                    return instance = new JdkProxySource();
                case "cglib":
                default:
                    return instance = new CglibProxySource();
            }
        }
    }
}
