package com.qipeng.qrpc.client.bio;

import com.qipeng.qrpc.client.AbstractRpcClient;
import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.util.SocketReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BioRpcClient extends AbstractRpcClient {

    private static final ThreadPoolExecutor clientListenExecutor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("clientListen-{}").build();
        clientListenExecutor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                      new ArrayBlockingQueue<>(10000), threadFactory);
    }

    @Getter
    private final ServerInfo serverInfo;

    private volatile Socket socket;

    public BioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        connect(serverInfo);
    }

    protected void doConnect(ServerInfo serverInfo) {
        try {
            Socket socket = new Socket(serverInfo.getHost(), serverInfo.getPort());
            this.socket = socket;
            setConnected(true);
            clientListenExecutor.submit(() -> listen(socket));
        } catch (IOException e) {
            throw new RpcException("连接服务器失败:" + serverInfo);
        }
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request) {
        if (isConnected() || socket == null || !socket.isClosed()) {
            connect(serverInfo);
        }
        byte[] bytes = RpcPacketSerializer.encode(request);
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            IOUtils.closeQuietly(socket, null);
            socket = null;
            setConnected(false);
            throw new RpcException("写数据失败");
        }
        RpcFuture future = new RpcFuture(request.getRequestId());
        return future.get();
    }

    private void listen(Socket socket) {
        while (isConnected() && !socket.isClosed()) {
            try {
                RpcResponse response = SocketReader.readRpcPacket(socket, RpcResponse.class);
                RpcFuture future = RpcFuture.futureMap.get(response.getRequestId());
                if (future != null) {
                    future.setResponse(response);
                    future.getLatch().countDown();
                }
            } catch (IOException e) {
                IOUtils.closeQuietly(socket, null);
                break;
            }
        }
    }
}
