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
        InvokerParam invokeParam = new InvokerParam();
        invokeParam.setClazz(method.getDeclaringClass());
        invokeParam.setMethodName(method.getName());
        invokeParam.setParamTypes(method.getParameterTypes());
        invokeParam.setParameters(args);
        context.setInvokeParam(invokeParam);
        List<ServerParam> serverParams = registry.getServerParam(method.getDeclaringClass().getName());
        context.setServerParams(serverParams);
        return InvocationHandlerChain.invoke(context);
    }
}
