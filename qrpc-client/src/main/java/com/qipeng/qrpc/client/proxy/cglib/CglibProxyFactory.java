package com.qipeng.qrpc.client.proxy.cglib;

import com.qipeng.qrpc.client.proxy.ProxyFactory;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyFactory extends ProxyFactory {

    public Object doCreateProxy(Class<?> clazz, RegistryConfig registryConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibRpcInterceptor(registryConfig));
        return enhancer.create();
    }
}
