package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.common.registry.RegistryConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class JdkProxySource implements ProxySource {

    public Object createProxy(Class<?> clazz, RegistryConfig registryConfig) {
        JdkProxyHandler handler = new JdkProxyHandler(registryConfig);
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
    }

    static class JdkProxyHandler extends BaseProxyHandler implements InvocationHandler {

        private final RegistryConfig registryConfig;

        public JdkProxyHandler(RegistryConfig registryConfig) {
            this.registryConfig = registryConfig;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            InvocationContext context = buildContext(method, args, registryConfig);
            return invoke(context);
        }
    }

}
