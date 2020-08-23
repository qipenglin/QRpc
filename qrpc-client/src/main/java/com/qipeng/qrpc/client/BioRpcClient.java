package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serializer.RpcPacketSerializer;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BioRpcClient implements RpcClient {

    @Getter
    private final ServerInfo serverInfo;

    private Socket socket;

    public BioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        doConnect(serverInfo);
    }

    private void doConnect(ServerInfo serverInfo) {
        Socket socket = new Socket(serverInfo.getHost(), serverInfo.getPort());
        this.socket = socket;
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request) {
        byte[] bytes = RpcPacketSerializer.encode(request);
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            throw new RpcException("写数据失败");
        }
        RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
        return rpcFuture.get();
    }

    private void listen() {
        try {
            InputStream inputStream = socket.getInputStream();
            inputStream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
