package com.qipeng.qrpc.server.nio;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.nio.NioDataCache;
import com.qipeng.qrpc.common.nio.NioDataReader;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.server.RpcInvoker;
import com.qipeng.qrpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/30 10:28 下午
 */
@Slf4j
public class SimpleNioRpcServer implements RpcServer {
    private static final Queue<SocketChannel> channelQueue = new LinkedBlockingDeque<>();
    private volatile static SimpleNioRpcServer instance;
    private final ExecutorService listenThreadPool;
    private final ThreadPoolExecutor invokeTheadPool;
    private volatile boolean isActivated;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private SimpleNioRpcServer() {
        ThreadFactory tf = new BasicThreadFactory.Builder().namingPattern("NioServerThread-%d").build();
        listenThreadPool = Executors.newSingleThreadExecutor(tf);
        invokeTheadPool = new ThreadPoolExecutor(10, 100, 1000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), tf);
    }

    public static SimpleNioRpcServer getInstance() {
        if (instance == null) {
            synchronized (SimpleNioRpcServer.class) {
                if (instance == null) {
                    instance = new SimpleNioRpcServer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public void start(ServerInfo serverInfo) {
        if (isActivated) {
            return;
        }
        try {
            InetSocketAddress address = new InetSocketAddress(serverInfo.getPort());
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            isActivated = true;
            listenThreadPool.submit(this::listen);
            log.info("NioRpcServer 启动成功，port:{}", serverInfo.getPort());
        } catch (IOException e) {
            throw new RpcException("NioRpcServer start 发生异常", e);
        }
    }

    private void listen() {
        while (isActivated && selector.isOpen()) {
            try {
                if (!channelQueue.isEmpty()) {
                    SocketChannel channel = channelQueue.poll();
                    channel.register(selector, SelectionKey.OP_READ);
                }
                selector.select(10);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isAcceptable()) {
                        SocketChannel sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        channelQueue.add(sc);
                        log.info("NioRpcServer收到客户端连接请求,remoteAddress:{}", sc.getRemoteAddress());
                    } else if (sk.isReadable()) {
                        read(sk);
                    }
                }
            } catch (Exception e) {
                log.error("NioServer listen exception", e);
            }
        }
    }

    private void read(SelectionKey sk) {
        try {
            NioDataReader.readData(sk);
            NioDataCache cache = (NioDataCache) sk.attachment();
            while (cache.isReady()) {
                byte[] bytes = cache.getData();
                RpcRequest request = RpcPacketSerializer.deserialize(bytes, RpcRequest.class);
                invokeTheadPool.submit(() -> invokeRpc(request, sk));
            }
        } catch (Exception e) {
            log.error("NioRpcServer read 发生异常", e);
            sk.cancel();
            IOUtils.closeQuietly(sk.channel(), null);
        }
    }

    private void invokeRpc(RpcRequest request, SelectionKey sk) {
        log.info("NioRpcServer request:{}", request);
        RpcResponse response = RpcInvoker.invoke(request);
        try {
            byte[] bytes = RpcPacketSerializer.serialize(response);
            ((SocketChannel) sk.channel()).write(ByteBuffer.wrap(bytes));
        } catch (Exception e) {
            log.error("NioRpcServer write 发生异常", e);
            sk.cancel();
            IOUtils.closeQuietly(sk.channel(), null);
        }
    }
}
