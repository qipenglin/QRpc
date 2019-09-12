package com.qipeng.qrpc.client;

import com.qipeng.qrpc.client.proxy.ProxyFactory;
import com.qipeng.qrpc.common.registry.Registry;

/**
 * @Author qipenglin
 * @Date 2019-09-12 17:38
 **/
public class RpcServiceFactory {

    private Registry registry;

    public RpcServiceFactory(Registry registry) {
        this.registry = registry;
    }

    public <T> Object getService(Class<T> clazz) {
        Object proxy = ProxyFactory.getProxy(clazz, registry);
        return proxy;
    }
}
