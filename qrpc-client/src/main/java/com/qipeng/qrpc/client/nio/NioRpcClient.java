package com.qipeng.qrpc.client.nio;

import com.qipeng.qrpc.client.RpcClient;
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
public class NioRpcClient implements RpcClient {

    @Getter
    private final ServerInfo serverInfo;

    private Selector selector;

    private SocketChannel channel;

    private static final ThreadPoolExecutor clientExecutor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("NioRpcClientThread-{}").build();
        clientExecutor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                new ArrayBlockingQueue<>(10000), threadFactory);
    }

    public NioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        doConnect(serverInfo);
        clientExecutor.execute(this::listen);
    }

    private void doConnect(ServerInfo serverInfo) {
        try {
            InetSocketAddress address = new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort());
            selector = Selector.open();
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RpcException("创建NioRpcClient失败", e);
        }
    }

    private void listen() {
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isReadable()) {
                        doRead(sk);
                    }
                }
            }
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private void doRead(SelectionKey sk) {
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
    public RpcResponse invokeRpc(RpcRequest request) {
        if (channel == null) {
            doConnect(serverInfo);
        }
        try {
            byte[] bytes = RpcPacketSerializer.encode(request);
            channel.write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            throw new RpcException("写数据失败");
        }
        RpcFuture future = new RpcFuture(request.getRequestId());
        return future.get();
    }
}
