package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;

import java.lang.reflect.Method;

public class RpcInvoker {

    public static RpcResponse invoke(RpcRequest request, ServiceProvider provider) {
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
