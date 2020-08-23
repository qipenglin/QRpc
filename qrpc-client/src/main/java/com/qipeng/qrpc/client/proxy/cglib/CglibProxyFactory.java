package com.qipeng.qrpc.client.proxy.cglib;

import com.qipeng.qrpc.common.registry.Registry;
import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyFactory {

    public static Object doCreateProxy(Class<?> clazz, Registry registry) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CglibRpcInterceptor(registry));
        return enhancer.create();
    }
}
