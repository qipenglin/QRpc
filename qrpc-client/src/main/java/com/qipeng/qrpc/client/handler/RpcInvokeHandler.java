package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.client.RpcClient;
import com.qipeng.qrpc.client.RpcClientFactory;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.exception.RpcException;

public class RpcInvokeHandler extends AbstractInvocationHandler {

    @Override
    public void doInvoke(InvocationContext context) {
        RpcRequest request = buildRpcRequest(context);
        RpcClient client = RpcClientFactory.getClient(context.getServerInfo());
        RpcResponse response = client.invokeRpc(request);
        if (response.getHasException()) {
            throw new RpcException((Throwable) response.getResult());
        }
        context.setResult(response.getResult());
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
