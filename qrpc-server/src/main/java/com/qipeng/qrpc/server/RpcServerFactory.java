package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.model.NetworkModel;
import com.qipeng.qrpc.server.bio.BioRpcServer;
import com.qipeng.qrpc.server.netty.NettyRpcServer;

public class RpcServerFactory {

    private static volatile RpcServer server;

    public static RpcServer getServer() {
        if (server != null) {
            return server;
        }
        synchronized (RpcServerFactory.class) {
            NetworkModel networkModel = NetworkModel.getByName(RpcConfig.NETWORK_MODEL);
            switch (networkModel) {
                case BIO:
                    server = new BioRpcServer();
                    break;
                case NETTY:
                default:
                    server = new NettyRpcServer();
            }
        }
        return server;
    }
}
