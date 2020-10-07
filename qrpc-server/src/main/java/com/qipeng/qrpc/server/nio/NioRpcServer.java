package com.qipeng.qrpc.server.nio;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

@Slf4j
public class NioRpcServer implements RpcServer {

    private ServerSocketChannel serverChannel;
    private volatile boolean isStarted;
    private volatile static NioRpcServer instance;

    private NioRpcServer() {
    }

    public static NioRpcServer getInstance() {
        if (instance == null) {
            synchronized (NioRpcServer.class) {
                if (instance == null) {
                    instance = new NioRpcServer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public void start(ServerInfo serverInfo) {
        if (isStarted) {
            return;
        }
        try {
            InetSocketAddress address = new InetSocketAddress(serverInfo.getPort());
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(address);
            serverChannel.configureBlocking(false);
            NioServerAcceptor acceptor = new NioServerAcceptor(serverChannel);
            acceptor.register(serverChannel, SelectionKey.OP_ACCEPT);
            isStarted = true;
            log.info("NioRpcServer 启动成功，port:{}", serverInfo.getPort());
        } catch (IOException e) {
            throw new RpcException("NioRpcServer start 发生异常", e);
        }
    }
}
