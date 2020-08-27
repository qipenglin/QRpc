package com.qipeng.qrpc.client.proxy.jdkproxy;

import com.qipeng.qrpc.common.registry.RegistryConfig;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {

    public static Object doCreateProxy(Class<?> clazz, RegistryConfig registryConfig) {
        JdkProxyRpcInterceptor interceptor = new JdkProxyRpcInterceptor(registryConfig);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, interceptor);
    }

}
