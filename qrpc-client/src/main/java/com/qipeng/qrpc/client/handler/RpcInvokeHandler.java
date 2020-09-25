package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.client.RpcClient;
import com.qipeng.qrpc.client.RpcClientFactory;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcResponse;

public class RpcInvokeHandler extends AbstractInvocationHandler {

    @Override
    public void doInvoke(InvocationContext context) {
        RpcClient client = RpcClientFactory.getClient(context.getServerInfo());
        RpcResponse response = client.invokeRpc(context.getRpcRequest());
        if (response.getHasException()) {
            throw new RpcException((Throwable) response.getResult());
        }
        context.setResult(response.getResult());
    }
}
