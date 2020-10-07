package com.qipeng.qrpc.client.nio;

import com.qipeng.qrpc.client.AbstractRpcClient;
import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.nio.event.NioEventLoopGroup;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@Slf4j
public class NioRpcClient extends AbstractRpcClient {

    @Getter
    private final ServerInfo serverInfo;

    private SocketChannel channel;

    private static final NioEventLoopGroup eventLoopGroup;

    private final NioClientEventLoop eventLoop;

    static {
        eventLoopGroup = new NioEventLoopGroup(NioClientEventLoop.group());
    }

    public NioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        eventLoop = (NioClientEventLoop)eventLoopGroup.next();
        connect(serverInfo);
    }

    @Override
    protected void doConnect(ServerInfo serverInfo) {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort());
            channel = SocketChannel.open(serverAddress);
            channel.configureBlocking(false);
            eventLoop.register(channel, SelectionKey.OP_READ);
            setConnected(true);
        } catch (Exception e) {
            throw new RpcException("NioRpcClient连接服务器失败,serverInfo:" + serverInfo, e);
        }
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request, int timeout) {
        log.info("NioRpcClient request:{}", request);
        RpcFuture future = new RpcFuture(request.getRequestId(), timeout);
        if (!isConnected() || channel == null || !channel.isConnected() || !channel.isOpen()) {
            doConnect(serverInfo);
        }
        eventLoop.execute(() -> write(request));
        return future.get();
    }

    private void write(RpcRequest request) {
        try {
            byte[] bytes = RpcPacketSerializer.serialize(request);
            channel.write(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            setConnected(false);
            IOUtils.closeQuietly(channel, null);
            throw new RpcException("NioRpcClient写数据失败,serverInfo:" + serverInfo, e);
        }
    }
}
