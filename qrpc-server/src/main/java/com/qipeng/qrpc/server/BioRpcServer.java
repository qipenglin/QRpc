package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.ServerInfo;

import java.net.ServerSocket;

public class BioRpcServer implements RpcServer {

    @Override
    public void start(ServerInfo serverInfo) {
        ServerSocket serverSocket = new ServerSocket();
    }
}
