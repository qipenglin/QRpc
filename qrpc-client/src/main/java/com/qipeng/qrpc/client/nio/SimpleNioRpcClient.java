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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/30 10:26 下午
 */
@Slf4j
public class SimpleNioRpcClient extends AbstractRpcClient {
    private static final Queue<SocketChannel> channelQueue = new LinkedBlockingDeque<>();
    private static final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    private static volatile boolean listenStarted;
    private static final Selector selector;

    @Getter
    private final ServerInfo serverInfo;
    private SocketChannel channel;

    static {
        try {
            selector = Selector.open();
        } catch (Exception e) {
            throw new RpcException("NioRpcClient初始化selector失败", e);
        }
    }

    public SimpleNioRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        connect(serverInfo);
    }

    @Override
    protected void doConnect(ServerInfo serverInfo) {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort());
            channel = SocketChannel.open(serverAddress);
            channel.configureBlocking(false);
            channelQueue.add(channel);
            setConnected(true);
            if (!listenStarted) {
                startListen();
            }
        } catch (IOException e) {
            throw new RpcException("NioRpcClient连接服务器失败,serverInfo:" + serverInfo, e);
        }
    }

    private synchronized void startListen() {
        if (!listenStarted) {
            listenExecutor.execute(SimpleNioRpcClient::listen);
            listenStarted = true;
        }
    }

    private static void listen() {
        while (selector.isOpen()) {
            try {
                if (!channelQueue.isEmpty()) {
                    SocketChannel channel = channelQueue.poll();
                    channel.register(selector, SelectionKey.OP_READ);
                }
                selector.select(20);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isReadable()) {
                        read(sk);
                    }
                }
            } catch (Exception e) {
                log.error("NioRpcClient listen 发生异常", e);
            }
        }
    }

    private static void read(SelectionKey sk) {
        NioDataReader.readData(sk);
        NioDataCache cache = (NioDataCache) sk.attachment();
        while (cache != null && cache.isReady()) {
            byte[] bytes = cache.getData();
            RpcResponse response = RpcPacketSerializer.deserialize(bytes, RpcResponse.class);
            RpcFuture future = RpcFuture.getFuture(response.getRequestId());
            if (future != null) {
                future.setResponse(response);
                future.getLatch().countDown();
            }
        }
    }

    @Override
    public RpcResponse invokeRpc(RpcRequest request, int timeout) {
        log.info("NioRpcClient request:{}", request);
        RpcFuture future = new RpcFuture(request.getRequestId(), timeout);
        if (!isConnected() || channel == null || !channel.isConnected() || !channel.isOpen()) {
            doConnect(serverInfo);
        }
        try {
            byte[] bytes = RpcPacketSerializer.serialize(request);
            channel.write(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            setConnected(false);
            IOUtils.closeQuietly(channel, null);
            throw new RpcException("NioRpcClient写数据失败,serverInfo:" + serverInfo, e);
        }
        return future.get();
    }
}
