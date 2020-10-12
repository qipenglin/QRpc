package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class CglibProxySource implements ProxySource {

    public Object createProxy(Class<?> clazz, RegistryConfig registryConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibProxyHandler(registryConfig));
        return enhancer.create();
    }

    static class CglibProxyHandler extends BaseProxyHandler implements MethodInterceptor {

        private final RegistryConfig registryConfig;

        public CglibProxyHandler(RegistryConfig registryConfig) {
            this.registryConfig = registryConfig;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            InvocationContext context = buildContext(method, args, registryConfig);
            return invoke(context);
        }
    }
}
