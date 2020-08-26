package com.qipeng.qrpc.client.proxy.jdkproxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.InvokerParam;
import com.qipeng.qrpc.common.registry.Registry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkProxyRpcInterceptor implements InvocationHandler {

    private final Registry registry;

    public JdkProxyRpcInterceptor(Registry registry) {
        this.registry = registry;
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
        context.setRegistry(registry);
        return InvocationHandlerChain.invoke(context);
    }
}
