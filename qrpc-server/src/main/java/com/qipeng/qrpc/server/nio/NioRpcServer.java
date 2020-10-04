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
import com.qipeng.qrpc.server.bio.BioRpcServer;
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
import java.util.concurrent.*;

/**
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/30 10:28 下午
 */
@Slf4j
public class NioRpcServer implements RpcServer {

    private volatile boolean isActivated;
    private ServerSocketChannel channel;
    private Selector selector;
    private final ExecutorService ioThreadPool;
    private final ThreadPoolExecutor invokeTheadPool;
    private volatile static NioRpcServer instance;

    private NioRpcServer() {
        ThreadFactory tf = new BasicThreadFactory.Builder().namingPattern("NioServerThread-%d").build();
        ioThreadPool = Executors.newSingleThreadExecutor(tf);
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
            ioThreadPool.submit(this::listen);
            log.info("NioRpcServer 启动成功，port:{}", serverInfo.getPort());
        } catch (IOException e) {
            throw new RpcException("NioRpcServer start 发生异常", e);
        }
    }

    private void listen() {
        while (isActivated) {
            try {
                selector.select(1000);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if (sk.isAcceptable()) {
                        accept();
                    } else if (sk.isReadable()) {
                        read(sk);
                    }
                }
            } catch (Exception e) {
                log.error("NioServer listen exception", e);
            }
        }
    }

    private void accept() {
        try {
            SocketChannel sc = channel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            log.info("NioRpcServer收到客户端连接请求,remoteAddress:{}", sc.getRemoteAddress());
        } catch (IOException e) {
            log.error("NioRpcServer accept 发生异常", e);
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
        RpcResponse response = RpcInvoker.invoke(request);
        ioThreadPool.execute(() -> {
            try {
                byte[] bytes = RpcPacketSerializer.serialize(response);
                ((SocketChannel) sk.channel()).write(ByteBuffer.wrap(bytes));
            } catch (Exception e) {
                log.error("NioRpcServer write 发生异常", e);
                sk.cancel();
                IOUtils.closeQuietly(sk.channel(), null);
            }
        });
    }
}
