package com.qipeng.qrpc.client.proxy;

import com.qipeng.qrpc.client.handler.InvocationContext;
import com.qipeng.qrpc.client.handler.InvocationHandlerChain;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.registry.RegistryConfig;

import java.lang.reflect.Method;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/10/12 12:02 下午
 */
public abstract class BaseProxyHandler implements ProxyHandler {

    protected InvocationContext buildContext(final Method method, final Object[] args, RegistryConfig registryConfig) {
        InvocationContext context = new InvocationContext();
        RpcRequest request = new RpcRequest();
        request.setClazz(method.getDeclaringClass());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParamTypes(method.getParameterTypes());
        context.setRpcRequest(request);
        context.setRegistryConfig(registryConfig);
        return context;
    }

    @Override
    public Object invoke(InvocationContext context) {
        return InvocationHandlerChain.invoke(context);
    }
}
