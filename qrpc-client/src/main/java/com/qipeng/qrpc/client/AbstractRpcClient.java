package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.model.ServerInfo;
import lombok.Getter;
import lombok.Setter;

/**
 *
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
    private volatile boolean isConnected;

    @Override
    public void connect(ServerInfo serverInfo) {
        if (isConnected) {
            return;
        }
        synchronized (this) {
            if (isConnected) {
                return;
            }
            doConnect(serverInfo);
        }
    }

    protected abstract void doConnect(ServerInfo serverInfo);
}
