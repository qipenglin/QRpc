package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;

public interface RpcClient {
    RpcResponse invokeRpc(RpcRequest request);
}
