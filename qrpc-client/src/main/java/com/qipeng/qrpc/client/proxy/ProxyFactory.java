package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.proxy.cglib.CglibRpcInterceptor;
import com.qipeng.qrpc.client.proxy.jdkproxy.JdkProxyRpcInterceptor;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {


    private static final Map<RegistryConfig, Map<String, Object>> REGISTRY_PROXY_MAP = new ConcurrentHashMap<>();

    public static Object getProxy(Class<?> clazz) {
        return getProxy(clazz, null);
    }

    public static Object getProxy(Class<?> clazz, RegistryConfig registryConfig) {
        Map<String, Object> proxyMap;
        if ((proxyMap = REGISTRY_PROXY_MAP.get(registryConfig)) != null) {
            return getProxy(clazz, registryConfig, proxyMap);
        }
        synchronized (ProxyFactory.class) {
            proxyMap = REGISTRY_PROXY_MAP.computeIfAbsent(registryConfig, r -> new ConcurrentHashMap<>());
        }
        return getProxy(clazz, registryConfig, proxyMap);
    }

    private static Object getProxy(Class<?> clazz, RegistryConfig registryConfig, Map<String, Object> proxyMap) {
        String className = clazz.getName();
        Object proxy = proxyMap.get(className);
        if (proxy != null) {
            return proxy;
        }
        synchronized (ProxyFactory.class) {
            if (proxyMap.get(className) == null) {
                proxy = doCreateCglibProxy(clazz, registryConfig);
                //proxy = doCreateJdkProxy(clazz, registryConfig);
                proxyMap.put(clazz.getName(), proxy);
            }
        }
        return proxyMap.get(className);
    }

    private static Object doCreateCglibProxy(Class<?> clazz, RegistryConfig registryConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibRpcInterceptor(registryConfig));
        return enhancer.create();
    }

    private static Object doCreateJdkProxy(Class<?> clazz, RegistryConfig registryConfig) {
        JdkProxyRpcInterceptor interceptor = new JdkProxyRpcInterceptor(registryConfig);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, interceptor);
    }
}
