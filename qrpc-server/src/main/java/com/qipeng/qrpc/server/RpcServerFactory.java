package com.qipeng.qrpc.server;

public class RpcServerFactory {

    private static volatile RpcServer server;

    public static RpcServer getServer() {
        if (server != null) {
            return server;
        }
        synchronized (RpcServerFactory.class) {
            if (server == null) {
                server = new RpcServer();
            }
        }
        return server;
    }
}
