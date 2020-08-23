package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.proxy.cglib.CglibRpcInterceptor;
import com.qipeng.qrpc.client.proxy.jdkproxy.JdkProxyRpcInterceptor;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactory {

    private static final Map<Registry, Map<String, Object>> REGISTRY_PROXY_MAP = new ConcurrentHashMap<>();

    public static Object getProxy(Class<?> clazz) {
        Registry registry = RegistryFactory.getDefaultRegistry();
        return getProxy(clazz, registry);
    }

    public static Object getProxy(Class<?> clazz, Registry registry) {
        if (registry == null) {
            registry = RegistryFactory.getDefaultRegistry();
        }
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
                proxy = doCreateCglibProxy(clazz, registry);
                //proxy = doCreateJdkProxy(clazz, registry);
                proxyMap.put(clazz.getName(), proxy);
            }
        }
        return proxyMap.get(className);
    }

    private static Object doCreateCglibProxy(Class<?> clazz, Registry registry) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibRpcInterceptor(registry));
        return enhancer.create();
    }

    private static Object doCreateJdkProxy(Class<?> clazz, Registry registry) {
        JdkProxyRpcInterceptor interceptor = new JdkProxyRpcInterceptor(registry);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, interceptor);
    }
}
