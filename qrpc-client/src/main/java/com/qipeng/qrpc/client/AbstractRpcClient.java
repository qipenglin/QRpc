package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/9/19 9:22 下午
 */
public abstract class AbstractRpcClient implements RpcClient {

    @Getter
    @Setter
    private volatile boolean connected;

    @Override
    public RpcResponse invokeRpc(RpcRequest request) {
        return invokeRpc(request, -1);
    }

    @Override
    public void connect(ServerInfo serverInfo) {
        if (isConnected()) {
            return;
        }
        synchronized (this) {
            if (isConnected()) {
                return;
            }
            try {
                doConnect(serverInfo);
            } catch (Exception exception) {
                throw new RpcException("连接服务器失败:" + serverInfo);
            }
        }
    }

    protected abstract void doConnect(ServerInfo serverInfo);
}
