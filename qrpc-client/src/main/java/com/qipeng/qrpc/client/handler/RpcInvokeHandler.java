package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.client.RpcClient;
import com.qipeng.qrpc.client.RpcClientFactory;
import com.qipeng.qrpc.common.RpcRequest;

public class RpcInvokeHandler extends AbstractInvocationHandler {

    @Override
    public Object invoke(InvocationContext context) {
        RpcRequest request = buildRpcRequest(context);
        RpcClient rpcClient = RpcClientFactory.getClient(context.getServerParam());
        return rpcClient.invokeRpc(request);
    }

    private RpcRequest buildRpcRequest(InvocationContext context) {
        RpcRequest request = new RpcRequest();
        request.setClazz(context.getInvokerParam().getClazz());
        request.setMethodName(context.getInvokerParam().getMethodName());
        request.setParamTypes(context.getParamTypes());
        request.setParameters(context.getParameters());
        return request;
    }
}
