package com.qipeng.qrpc.server;

import com.qipeng.qrpc.server.netty.NettyRpcServer;

public class RpcServerFactory {

    private static volatile NettyRpcServer server;

    public static NettyRpcServer getServer() {
        if (server != null) {
            return server;
        }
        synchronized (RpcServerFactory.class) {
            if (server == null) {
                server = new NettyRpcServer();
            }
        }
        return server;
    }
}
