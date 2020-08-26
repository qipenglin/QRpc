package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;

public interface RpcClient {
    RpcResponse invokeRpc(RpcRequest request);
}
