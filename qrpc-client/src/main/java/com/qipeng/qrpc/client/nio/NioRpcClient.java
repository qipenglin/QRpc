package com.qipeng.qrpc.client.nio;

import com.qipeng.qrpc.client.AbstractRpcClient;
import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.nio.NioDataCache;
import com.qipeng.qrpc.common.nio.NioDataReader;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.util.ByteUtils;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/30 10:26 下午
 */
public class NioRpcClient extends AbstractRpcClient {
    private static final Selector selector;
    private static final ThreadPoolExecutor clientExecutor;
    @Getter
    private final ServerInfo serverInfo;
    private SocketChannel channel;
    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RpcException("初始化selector失败", e);
        }
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("NioRpcClientThread-{}").build();
        clientExecutor = new ThreadPoolExecutor(10, 10, 1000L, TimeUnit.SECONDS,
                                                new ArrayBlockingQueue<>(10000), threadFactory);
        clientExecutor.execute(NioRpcClient::listen);
    }

    public NioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        connect(serverInfo);
    }

    @Override
    protected void doConnect(ServerInfo serverInfo) {
        try {
            InetSocketAddress address = new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort());
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            setConnected(true);
        } catch (IOException e) {
            throw new RpcException("NioRpcClient连接服务器失败,serverInfo:" + serverInfo, e);
        }
    }

    private static void listen() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isReadable()) {
                        clientExecutor.execute(() -> doRead(sk));
                    }
                }
            }
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private static void doRead(SelectionKey sk) {
        NioDataReader.readData(sk);
        NioDataCache cache = (NioDataCache) sk.attachment();
        while (cache.isReady()) {
            byte[] bytes = cache.getData();
            RpcResponse response = ByteUtils.deserialize(bytes, RpcResponse.class);
            RpcFuture future = RpcFuture.futureMap.get(response.getRequestId());
            if (future != null) {
                future.setResponse(response);
                future.getLatch().countDown();
            }
        }
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request, int timeout) {
        RpcFuture future = new RpcFuture(request.getRequestId(), timeout);
        if (channel == null || !channel.isConnected()) {
            doConnect(serverInfo);
        }
        try {
            byte[] bytes = RpcPacketSerializer.encode(request);
            channel.write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            setConnected(false);
            IOUtils.closeQuietly(channel, null);
            throw new RpcException("写数据失败");
        }
        return future.get();
    }
}
