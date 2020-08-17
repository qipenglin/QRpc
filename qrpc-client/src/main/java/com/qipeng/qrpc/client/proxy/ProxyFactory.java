package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {

    private static final Map<Registry, Map<String, Object>> REGISTRY_PROXY_MAP = new ConcurrentHashMap<>();

    public static Object getProxy(Class<?> clazz) {
        Registry registry = RegistryFactory.getDefaultRegistry();
        return getProxy(clazz, registry);
    }

    public static Object getProxy(Class<?> clazz, Registry registry) {
        Map<String, Object> proxyMap;
        if ((proxyMap = REGISTRY_PROXY_MAP.get(registry)) != null) {
            return getProxy(clazz, registry, proxyMap);
        }
        synchronized (registry) {
            proxyMap = REGISTRY_PROXY_MAP.computeIfAbsent(registry, r -> new ConcurrentHashMap<>());
        }
        return getProxy(clazz, registry, proxyMap);
    }

    private static Object getProxy(Class<?> clazz, Registry registry, Map<String, Object> proxyMap) {
        String className = clazz.getName();
        Object proxy = proxyMap.get(className);
        if (proxy != null) {
            return proxy;
        }
        synchronized (clazz) {
            if (proxyMap.get(className) == null) {
                proxy = doCreateProxy(clazz, registry);
                proxyMap.put(clazz.getName(), proxy);
            }
        }
        return proxyMap.get(className);
    }

    private static Object doCreateProxy(Class<?> clazz, Registry registry) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new RpcInterceptor(registry));
        return enhancer.create();
    }
}
