package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.model.NetworkModel;
import com.qipeng.qrpc.server.bio.BioRpcServer;
import com.qipeng.qrpc.server.netty.NettyRpcServer;
import com.qipeng.qrpc.server.nio.NioRpcServer;
import com.qipeng.qrpc.server.nio.SimpleNioRpcServer;

public class RpcServerFactory {

    private static volatile RpcServer server;

    public static RpcServer getServer() {
        if (server != null) {
            return server;
        }
        synchronized (RpcServerFactory.class) {
            if (server != null) {
                return server;
            }
            NetworkModel networkModel = NetworkModel.getByName(RpcConfig.getNetworkModel());
            switch (networkModel) {
                case BIO:
                    server = BioRpcServer.getInstance();
                    break;
                case NIO:
                    server = NioRpcServer.getInstance();
                    break;
                case SIMPLE_NIO:
                    server = SimpleNioRpcServer.getInstance();
                    break;
                case NETTY:
                default:
                    server = NettyRpcServer.getInstance();
            }
        }
        return server;
    }
}
