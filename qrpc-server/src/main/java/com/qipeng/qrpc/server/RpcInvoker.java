package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcInvoker {

    public static RpcResponse invoke(RpcRequest request) {
        String serviceName = request.getClazz().getName();
        log.info("收到rpc请求,serviceName:{},method:{}", serviceName, request.getMethodName());
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        ServiceProvider provider = RpcServer.PROVIDER_MAP.get(serviceName);
        if (provider == null) {
            response.setHasException(true);
            response.setResult(new RpcException("Service " + serviceName + " 不存在"));
            return response;
        }
        try {
            Object instance = provider.getInstance();
            String methodName = request.getMethodName();
            Class<?>[] paramTypes = request.getParamTypes();
            Method method = instance.getClass().getMethod(methodName, paramTypes);
            Object[] params = request.getParameters();
            Object result = method.invoke(instance, params);
            response.setResult(result);
            response.setHasException(false);
        } catch (Throwable e) {
            response.setHasException(true);
            response.setResult(e);
        }
        return response;
    }
}
