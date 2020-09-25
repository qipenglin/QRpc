package com.qipeng.qrpc.client.proxy.cglib;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibRpcInterceptor implements MethodInterceptor {

    private final RegistryConfig registryConfig;

    public CglibRpcInterceptor(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        InvocationContext context = new InvocationContext();
        RpcRequest request = new RpcRequest();
        request.setClazz(method.getDeclaringClass());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParamTypes(method.getParameterTypes());
        context.setRpcRequest(request);
        context.setRegistryConfig(registryConfig);
        return InvocationHandlerChain.invoke(context);
    }
}
