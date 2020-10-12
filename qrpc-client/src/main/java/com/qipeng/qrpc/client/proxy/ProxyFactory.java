package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.registry.RegistryConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {

    private static final Map<RegistryConfig, Map<String, Object>> REGISTRY_PROXY_MAP = new HashMap<>();

    public static Object getProxy(Class<?> clazz) {
        return getProxy(clazz, null);
    }

    public static <T> Object getProxy(Class<T> clazz, RegistryConfig registryConfig) {
        Map<String, Object> proxyMap;
        if ((proxyMap = REGISTRY_PROXY_MAP.get(registryConfig)) != null) {
            return getProxy(clazz, registryConfig, proxyMap);
        }
        synchronized (ProxyFactory.class) {
            proxyMap = REGISTRY_PROXY_MAP.computeIfAbsent(registryConfig, r -> new ConcurrentHashMap<>());
        }
        return getProxy(clazz, registryConfig, proxyMap);
    }

    private static <T> Object getProxy(Class<T> clazz, RegistryConfig registryConfig, Map<String, Object> proxyMap) {
        String className = clazz.getName();
        Object proxy = proxyMap.get(className);
        if (proxy != null) {
            return proxy;
        }
        synchronized (ProxyFactory.class) {
            if (proxyMap.get(className) == null) {
                proxy = doCreateProxy(clazz, registryConfig);
                proxyMap.put(clazz.getName(), proxy);
            }
        }
        return proxyMap.get(className);
    }

    private static <T> Object doCreateProxy(Class<T> clazz, RegistryConfig registryConfig) {
        ProxySource proxySource = ProxySourceFactory.getInstance();
        return proxySource.createProxy(clazz, registryConfig);
    }
}
