package com.qipeng.qrpc.client.proxy.cglib;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.InvokerParam;
import com.qipeng.qrpc.common.registry.Registry;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibRpcInterceptor implements MethodInterceptor {

    private final Registry registry;

    public CglibRpcInterceptor(Registry registry) {
        this.registry = registry;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
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
