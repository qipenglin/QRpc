package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.registry.RegistryConfig;

/**
 * @author qipenglin
 * @date 2019-09-12 17:38
 **/
public class RpcServiceFactory {

    private RegistryConfig registryConfig;

    public RpcServiceFactory(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    public <T> Object getService(Class<T> clazz) {
        return ProxyFactory.getProxy(clazz, registryConfig);
    }
}
