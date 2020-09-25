package com.qipeng.qrpc.server.nio;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.nio.NioDataCache;
import com.qipeng.qrpc.common.nio.NioDataReader;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.common.util.ByteUtils;
import com.qipeng.qrpc.server.RpcInvoker;
import com.qipeng.qrpc.server.RpcServer;
import com.qipeng.qrpc.server.bio.BioRpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
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
 * @date Created at 2020/8/30 10:28 下午
 */
@Slf4j
public class NioRpcServer implements RpcServer {
    /**
     * 服务端是否已经启动
     */
    private volatile boolean isActivated;
    private ServerSocketChannel channel;
    private Selector selector;
    private final ThreadPoolExecutor listenThreadPool;
    private final ThreadPoolExecutor rwThreadPool;
    private final ThreadPoolExecutor invokeTheadPool;
    private volatile static NioRpcServer instance;

    private NioRpcServer() {
        ThreadFactory tf = new BasicThreadFactory.Builder().namingPattern("NioServerThread-{}").build();
        listenThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, tf);
        rwThreadPool = new ThreadPoolExecutor(4, 8, 100L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), tf);
        invokeTheadPool = new ThreadPoolExecutor(10, 100, 1000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), tf);
    }

    public static NioRpcServer getInstance() {
        if (instance == null) {
            synchronized (BioRpcServer.class) {
                if (instance == null) {
                    instance = new NioRpcServer();
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
            channel = ServerSocketChannel.open();
            selector = Selector.open();
            channel.bind(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            isActivated = true;
            listenThreadPool.submit(this::listen);
        } catch (IOException e) {
            throw new RpcException("启动服务器失败");
        }
    }

    private void listen() {
        try {
            while (isActivated && selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isAcceptable()) {
                        accept();
                    } else if (sk.isReadable()) {
                        rwThreadPool.submit(() -> read(sk));
                    }
                }
            }
        } catch (Exception e) {
            log.error("NioServer listen exception", e);
            this.listen();
        }
    }

    private void accept() {
        try {
            SocketChannel sc = channel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            log.info("NioRpcServer收到客户端连接请求,remoteAddress:{}", sc.getRemoteAddress());
        } catch (IOException e) {
            throw new RpcException();
        }
    }

    private void read(SelectionKey sk) {
        try {
            NioDataReader.readData(sk);
            NioDataCache cache = (NioDataCache) sk.attachment();
            while (cache.isReady()) {
                byte[] bytes = cache.getData();
                RpcRequest request = ByteUtils.deserialize(bytes, RpcRequest.class);
                invokeTheadPool.submit(() -> invokeRpc(request, sk));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sk.cancel();
        }
    }

    private void invokeRpc(RpcRequest request, SelectionKey sk) {
        RpcResponse response = RpcInvoker.invoke(request);
        byte[] bytes = RpcPacketSerializer.encode(response);
        rwThreadPool.execute(() -> {
            try {
                ((SocketChannel) sk.channel()).write(ByteBuffer.wrap(bytes));
            } catch (IOException e) {
                throw new RpcException("执行Rpc失败", e);
            }
        });
    }
}
