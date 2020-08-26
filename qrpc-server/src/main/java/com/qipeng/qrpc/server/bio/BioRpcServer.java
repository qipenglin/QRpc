package com.qipeng.qrpc.server.bio;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.serialize.SocketReader;
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

    private static final ThreadPoolExecutor listenExecutor;

    private static final ThreadPoolExecutor invokeExecutor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("requestDealer-{}").build();
        listenExecutor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                new ArrayBlockingQueue<>(10000), threadFactory);

        invokeExecutor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                new ArrayBlockingQueue<>(10000), threadFactory);
    }

    @Override
    public void start(ServerInfo serverInfo) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(serverInfo.getPort());
            log.info("BioRpcServer启动成功:serverInfo:{}", serverInfo);
            isActivated = true;
        } catch (IOException e) {
            throw new RpcException("启动RpcServer失败:" + serverInfo, e);
        }
        while (isActivated) {
            try {
                Socket socket = serverSocket.accept();
                log.info("客户端已连接,remoteAddress:{}", socket.getInetAddress());
                listenExecutor.execute(() -> listen(socket));
            } catch (Exception e) {
                log.error("accept失败");
            }
        }
    }

    private void listen(Socket socket) {
        while (isActivated) {
            RpcRequest request;
            try {
                request = SocketReader.readRpcPacket(socket, RpcRequest.class);
            } catch (IOException e) {
                break;
            }
            invokeExecutor.submit(() -> {
                RpcResponse response = RpcInvoker.invoke(request);
                byte[] bytes = RpcPacketSerializer.encode(response);
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
