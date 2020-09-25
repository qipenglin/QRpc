package com.qipeng.qrpc.client.proxy.jdkproxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.registry.RegistryConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkProxyRpcInterceptor implements InvocationHandler {

    private final RegistryConfig registryConfig;

    public JdkProxyRpcInterceptor(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationContext context = new InvocationContext();
        RpcRequest request = new RpcRequest();
        request.setClazz(method.getDeclaringClass());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParameters(args);
        context.setRpcRequest(request);
        context.setRegistryConfig(registryConfig);
        return InvocationHandlerChain.invoke(context);
    }
}
