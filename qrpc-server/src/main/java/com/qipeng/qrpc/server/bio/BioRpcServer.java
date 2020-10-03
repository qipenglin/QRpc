package com.qipeng.qrpc.server.bio;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.util.SocketReader;
import com.qipeng.qrpc.server.RpcInvoker;
import com.qipeng.qrpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BioRpcServer implements RpcServer {
    /**
     * 服务端是否已经启动
     */
    private volatile boolean isActivated;

    private final ThreadPoolExecutor serverThreadPool;

    private ServerSocket serverSocket;

    private volatile static BioRpcServer instance;

    private BioRpcServer() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("BioServerThread-{}")
                                                                      .build();
        serverThreadPool = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                  new ArrayBlockingQueue<>(10000), threadFactory);
    }

    public static BioRpcServer getInstance() {
        if (instance == null) {
            synchronized (BioRpcServer.class) {
                if (instance == null) {
                    instance = new BioRpcServer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public void start(ServerInfo serverInfo) {
        try {
            serverSocket = new ServerSocket(serverInfo.getPort());
            log.info("BioRpcServer启动成功:serverInfo:{}", serverInfo);
            isActivated = true;
        } catch (IOException e) {
            throw new RpcException("启动RpcServer失败:" + serverInfo, e);
        }
        serverThreadPool.execute(this::accept);
    }

    private void accept() {
        while (isActivated) {
            try {
                Socket socket = serverSocket.accept();
                log.info("客户端已连接,remoteAddress:{}", socket.getInetAddress());
                serverThreadPool.execute(() -> listen(socket));
            } catch (Exception e) {
                log.error("accept失败", e);
            }
        }
    }

    private void listen(Socket socket) {
        while (isActivated && socket != null && !socket.isClosed()) {
            RpcRequest request;
            try {
                request = SocketReader.readRpcPacket(socket, RpcRequest.class);
            } catch (IOException e) {
                break;
            }
            serverThreadPool.submit(() -> {
                RpcResponse response = RpcInvoker.invoke(request);
                byte[] bytes = RpcPacketSerializer.serialize(response);
                try {
                    socket.getOutputStream().write(bytes);
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    log.error("socket写数据失败");
                    IOUtils.closeQuietly(socket, null);
                }
            });
        }
        IOUtils.closeQuietly(socket, null);
    }

}
