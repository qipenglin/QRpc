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
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("BioRpcServer-%d")
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
            serverThreadPool.execute(this::accept);
        } catch (Exception e) {
            throw new RpcException("启动BioRpcServer失败:" + serverInfo, e);
        }

    }

    private void accept() {
        while (isActivated) {
            try {
                Socket socket = serverSocket.accept();
                log.info("客户端已连接,remoteAddress:{}", socket.getInetAddress());
                serverThreadPool.execute(() -> read(socket));
            } catch (Exception e) {
                log.error("BioRpcServer accept 发生异常", e);
            }
        }
    }

    private void read(Socket socket) {
        while (isActivated && socket != null && socket.isConnected() && !socket.isClosed()) {
            try {
                RpcRequest request = SocketReader.readRpcPacket(socket, RpcRequest.class);
                serverThreadPool.submit(() -> invokeRpc(request, socket));
            } catch (Exception e) {
                log.error("BioRpcServer listenAndRead 发生异常", e);
                break;
            }
        }
        IOUtils.closeQuietly(socket, null);
    }

    private void invokeRpc(RpcRequest request, Socket socket) {
        log.info("BioRpcServer request:{}", request);
        RpcResponse response = RpcInvoker.invoke(request);
        serverThreadPool.submit(() -> {
            try {
                byte[] bytes = RpcPacketSerializer.serialize(response);
                socket.getOutputStream().write(bytes);
                socket.getOutputStream().flush();
            } catch (Exception e) {
                log.error("BioRpcServer write 发生异常", e);
                IOUtils.closeQuietly(socket, null);
            }
        });
    }

}
