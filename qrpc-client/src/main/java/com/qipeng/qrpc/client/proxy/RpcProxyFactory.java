package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.registry.RegistryConfig;
import com.qipeng.qrpc.common.registry.RegistryFactory;

import java.util.HashMap;
import java.util.Map;

public class RpcProxyFactory {

    private static final Map<String, Object> PROXY_MAP = new HashMap<>();

    public static <T> T createProxy(Class<T> clazz) {
        RegistryConfig registryConfig = RegistryFactory.getDefaultRegistryConfig();
        return createProxy(clazz, registryConfig);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> clazz, RegistryConfig registryConfig) {
        String proxyKey = registryConfig + "/" + clazz.getName();
        T proxy = (T) PROXY_MAP.get(proxyKey);
        if (proxy != null) {
            return  proxy;
        }
        synchronized (RpcProxyFactory.class) {
            return (T) PROXY_MAP.computeIfAbsent(proxyKey, k -> doCreateProxy(clazz, registryConfig));
        }
    }

    private static <T> Object doCreateProxy(Class<T> clazz, RegistryConfig registryConfig) {
        ProxySource proxySource = ProxySourceFactory.getInstance();
        return proxySource.createProxy(clazz, registryConfig);
    }
}
