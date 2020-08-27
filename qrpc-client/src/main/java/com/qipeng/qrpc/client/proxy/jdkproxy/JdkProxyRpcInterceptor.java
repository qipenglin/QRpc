package com.qipeng.qrpc.client.proxy.jdkproxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.InvokerParam;
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
        InvokerParam invokeParam = new InvokerParam();
        invokeParam.setClazz(method.getDeclaringClass());
        invokeParam.setMethodName(method.getName());
        invokeParam.setParamTypes(method.getParameterTypes());
        invokeParam.setParameters(args);
        context.setInvokeParam(invokeParam);
        context.setRegistryConfig(registryConfig);
        return InvocationHandlerChain.invoke(context);
    }
}
