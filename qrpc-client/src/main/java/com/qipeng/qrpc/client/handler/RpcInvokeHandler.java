package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.client.RpcClient;
import com.qipeng.qrpc.client.RpcClientFactory;
import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.exception.RpcException;

public class RpcInvokeHandler extends AbstractInvocationHandler {

    @Override
    public Object doInvoke(InvocationContext context) {
        RpcRequest request = buildRpcRequest(context);
        RpcClient rpcClient = RpcClientFactory.getClient(context.getServerParam());
        RpcResponse response = rpcClient.invokeRpc(request);
        if (response.getHasException()) {
            return new RpcException((Throwable) response.getResult());
        }
        return response.getResult();
    }

    private RpcRequest buildRpcRequest(InvocationContext context) {
        RpcRequest request = new RpcRequest();
        request.setClazz(context.getInvokeParam().getClazz());
        request.setMethodName(context.getInvokeParam().getMethodName());
        request.setParamTypes(context.getInvokeParam().getParamTypes());
        request.setParameters(context.getInvokeParam().getParameters());
        return request;
    }
}
