package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.InvokerParam;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RpcInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        InvocationContext context = new InvocationContext();
        InvokerParam invokerParam = new InvokerParam();
        invokerParam.setClazz(o.getClass());
        invokerParam.setMethodName(method.getName());
        context.setInvokerParam(invokerParam);
        context.setParamTypes(method.getParameterTypes());
        context.setParameters(args);
        return InvocationHandlerChain.invoke(context);
    }
}
