package com.qipeng.qrpc.client.proxy.jdkproxy;

import com.qipeng.qrpc.common.registry.Registry;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {

    public static Object doCreateProxy(Class<?> clazz, Registry registry) {
        JdkProxyRpcInterceptor interceptor = new JdkProxyRpcInterceptor(registry);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, interceptor);
    }

}
