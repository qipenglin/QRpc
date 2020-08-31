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

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private static final ThreadPoolExecutor serverThreadPool;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("NioServerThread-{}").build();
        serverThreadPool = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                  new ArrayBlockingQueue<>(10000), threadFactory);
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
            serverThreadPool.submit(this::listen);
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
                        doAccept();
                    } else if (sk.isReadable()) {
                        doRead(sk);
                    }
                }
            }
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private void doAccept() {
        try {
            SocketChannel sc = serverSocketChannel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            log.info("NioRpcServer收到客户端连接请求,remoteAddress:{}", sc.getRemoteAddress());
        } catch (IOException e) {
            throw new RpcException();
        }
    }

    private void doRead(SelectionKey sk) {
        try {
            NioDataReader.readData(sk);
            NioDataCache cache = (NioDataCache) sk.attachment();
            while (cache.isReady()) {
                byte[] bytes = cache.getData();
                RpcRequest request = ByteUtils.deserialize(bytes, RpcRequest.class);
                serverThreadPool.submit(() -> invokeRpc(request, sk));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sk.cancel();
        }
    }

    private void invokeRpc(RpcRequest request, SelectionKey sk) {
        try {
            RpcResponse response = RpcInvoker.invoke(request);
            byte[] bytes = RpcPacketSerializer.encode(response);
            ((SocketChannel) sk.channel()).write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            throw new RpcException("执行Rpc失败", e);
        }
    }
}
