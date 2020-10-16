package com.qipeng.qrpc.server.nio;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.nio.NioDataCache;
import com.qipeng.qrpc.common.nio.NioDataReader;
import com.qipeng.qrpc.common.nio.event.NioEventLoop;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import com.qipeng.qrpc.server.RpcInvoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NioServerEventLoop extends NioEventLoop {

    private static final ThreadPoolExecutor invokeTheadPool;

    static {
        invokeTheadPool = new ThreadPoolExecutor(10, 100, 1000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
    }

    public NioServerEventLoop() {
        super();
    }

    public static NioServerEventLoop[] group() {
        int n = Runtime.getRuntime().availableProcessors();
        return group(n);
    }

    public static NioServerEventLoop[] group(int size) {
        size = size > 0 ? size : Runtime.getRuntime().availableProcessors();
        NioServerEventLoop[] arr = new NioServerEventLoop[size];
        for (int i = 0; i < size; i++) {
            arr[i] = new NioServerEventLoop();
        }
        return arr;
    }

    @Override
    protected void select() {
        try {
            selector.select(10);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                if (sk.isReadable()) {
                    read(sk);
                }
            }
        } catch (Exception e) {
            log.error("NioServerEventLoop select error", e);
        }
    }

    private void read(SelectionKey sk) {
        NioDataReader.readData(sk);
        NioDataCache cache = (NioDataCache) sk.attachment();
        while (cache != null && cache.isReady()) {
            byte[] bytes = cache.getData();
            if (bytes == null) {
                continue;
            }
            RpcRequest request = RpcPacketSerializer.deserialize(bytes, RpcRequest.class);
            invokeTheadPool.execute(() -> invokeRpc(request, sk));
        }
    }

    private void invokeRpc(RpcRequest request, SelectionKey sk) {
        log.info("NioRpcServer request:{}", request);
        RpcResponse response = RpcInvoker.invoke(request);
        this.execute(() -> write(sk, response));
    }

    private void write(SelectionKey sk, RpcResponse response) {
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
