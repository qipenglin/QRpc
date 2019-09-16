package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.InvokerParam;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.registry.Registry;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class RpcInterceptor implements MethodInterceptor {

    private Registry registry;

    private ServerParam serverParam;

    RpcInterceptor(Registry registry) {
        this.registry = registry;
    }

    RpcInterceptor(ServerParam serverParam) {
        this.serverParam = serverParam;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        InvocationContext context = new InvocationContext();
        InvokerParam invokerParam = new InvokerParam();
        invokerParam.setClazz(method.getDeclaringClass());
        invokerParam.setMethodName(method.getName());
        invokerParam.setParamTypes(method.getParameterTypes());
        invokerParam.setParameters(args);

        context.setInvokerParam(invokerParam);
        List<ServerParam> serverParams = registry.getServerParam(method.getDeclaringClass().getName());
        context.setServerParams(serverParams);
        return InvocationHandlerChain.invoke(context);
    }
}
