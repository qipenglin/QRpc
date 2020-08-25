package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerInfo;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.serialize.SocketReader;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BioRpcServer implements RpcServer {

    private static ThreadPoolExecutor executor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("requestDealer-{}").build();
        executor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000), threadFactory);
    }

    @Override
    public void start(ServerInfo serverInfo) {
        ServerSocket serverSocket = new ServerSocket(serverInfo.getPort());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(() -> {
                    try {
                        dealWithSocket(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                log.error("accept失败");
            }
        }
    }

    private void dealWithSocket(Socket socket) throws IOException {
        while (true) {
            RpcRequest request;
            try {
                request = SocketReader.readRpcPacket(socket, RpcRequest.class);
            } catch (IOException e) {
                break;
            }
            executor.submit(() -> {
                RpcResponse response = RpcInvoker.invoke(request);
                byte[] bytes = RpcPacketSerializer.encode(response);
                try {
                    socket.getOutputStream().write(bytes);
                } catch (IOException e) {
                    log.error("socket写数据失败");
                    try {
                        socket.close();
                    } catch (Exception e1) {
                    }
                }
            });
        }

    }

}
