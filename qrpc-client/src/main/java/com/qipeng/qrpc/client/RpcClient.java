package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;

public interface RpcClient {
    /**
     *  执行Rpc请求
     * @param request Rpc请求参数
     * @return Rpc响应
     */
    RpcResponse invokeRpc(RpcRequest request);

    /**
     * 与服务器建立连接
     * @param serverInfo 服务器地址参数
     */
    void connect(ServerInfo serverInfo);
}
