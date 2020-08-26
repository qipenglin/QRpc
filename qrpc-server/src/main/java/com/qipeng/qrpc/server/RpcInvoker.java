package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;

import java.lang.reflect.Method;

public class RpcInvoker {

    public static RpcResponse invoke(RpcRequest request) {
        String serviceName = request.getClazz().getName();
        ServiceProvider provider = RpcServer.PROVIDER_MAP.get(serviceName);
        if (provider == null) {
            throw new RuntimeException("服务不存在");
        }
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object instance = provider.getInstance();
            String methodName = request.getMethodName();
            Class<?>[] paramTypes = request.getParamTypes();
            Method method = instance.getClass().getMethod(methodName, paramTypes);
            Object[] params = request.getParameters();
            Object result = method.invoke(instance, params);
            response.setResult(result);
            response.setHasException(false);
        } catch (Exception e) {
            response.setHasException(true);
            response.setResult(e);
        }
        return response;
    }
}
