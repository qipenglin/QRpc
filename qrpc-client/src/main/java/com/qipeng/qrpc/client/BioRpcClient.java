package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcPacket;
import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serializer.RpcPacketSerializer;
import com.qipeng.qrpc.common.serializer.Serializer;
import com.qipeng.qrpc.common.serializer.SerializerFactory;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class BioRpcClient implements RpcClient {

    private static final byte MAGIC_NUM = 127;

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
        new Thread(this::loop).start();
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

    private void loop() {
        while (true) {
            try {
                InputStream inputStream = socket.getInputStream();
                checkMagicNum(inputStream);
                Serializer serializer = parseSerializer(inputStream);
                checkPacketType(inputStream);
                int len = parseLength(inputStream);
                byte[] bytes = getBody(inputStream, len);
                RpcResponse response = serializer.deserialize(RpcResponse.class, bytes);
                RpcFuture rpcFuture = RpcFuture.futureMap.get(response.getRequestId());
                if (rpcFuture != null) {
                    rpcFuture.setResponse(response);
                    rpcFuture.getLatch().countDown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int parseLength(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, 4);
        int len = byte4ToInt(bytes, 0);
        return len;
    }


    private void checkMagicNum(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        if (bytes[0] != MAGIC_NUM) {
            throw new RpcException("Magic No is incorrect");
        }
    }

    private void checkPacketType(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        Byte packetType = bytes[0];
        if (!RpcPacket.PacketType.RESPONSE.equals(packetType)) {
            throw new RpcException("Magic No is incorrect");
        }
    }

    private Serializer parseSerializer(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        return SerializerFactory.getSerializer(bytes[0]);
    }

    private byte[] getBody(InputStream inputStream, int len) throws IOException {
        byte[] bytes;
        bytes = new byte[len];
        inputStream.read(bytes, 0, len);
        return bytes;
    }

    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }


}
