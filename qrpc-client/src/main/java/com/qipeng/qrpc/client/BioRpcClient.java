package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.serialize.SocketReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class BioRpcClient implements RpcClient {

    @Getter
    private final ServerInfo serverInfo;

    private volatile Socket socket;

    public BioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        doConnect(serverInfo);
        new Thread(this::loop).start();
    }

    private void doConnect(ServerInfo serverInfo) {
        Socket socket;
        try {
            socket = new Socket(serverInfo.getHost(), serverInfo.getPort());
        } catch (IOException e) {
            throw new RpcException("连接服务器失败:" + serverInfo);
        }
        this.socket = socket;
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request) {
        if (socket == null) {
            doConnect(serverInfo);
        }
        byte[] bytes = RpcPacketSerializer.encode(request);
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            throw new RpcException("写数据失败");
        }
        RpcFuture future = new RpcFuture(request.getRequestId());
        return future.get();
    }

    private void loop() {
        while (true) {
            try {
                RpcResponse response = SocketReader.readRpcPacket(socket, RpcResponse.class);
                RpcFuture future = RpcFuture.futureMap.get(response.getRequestId());
                if (future != null) {
                    future.setResponse(response);
                    future.getLatch().countDown();
                }
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (Exception exception) {
                    log.error("关闭Socket失败");
                } finally {
                    socket = null;
                }
                break;
            }
        }
    }
}
