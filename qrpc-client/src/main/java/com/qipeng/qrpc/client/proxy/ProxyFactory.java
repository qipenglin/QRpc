package com.qipeng.qrpc.client.proxy;

import org.springframework.cglib.proxy.Enhancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {

    private static final Map<String, Object> proxyMap = new ConcurrentHashMap<>();

    public static Object getProxy(Class clazz) {
        String className = clazz.getName();
        Object proxy = proxyMap.get(className);
        if (proxy != null) {
            return proxy;
        }
        synchronized (clazz) {
            if (proxyMap.get(className) == null) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(new RpcInterceptor());
                proxy = enhancer.create();
                proxyMap.put(clazz.getName(), proxy);
                return proxy;
            }
        }
        return proxyMap.get(className);
    }
}
