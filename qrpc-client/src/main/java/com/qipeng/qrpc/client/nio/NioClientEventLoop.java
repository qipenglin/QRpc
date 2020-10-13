package com.qipeng.qrpc.client.nio;

import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.nio.NioDataCache;
import com.qipeng.qrpc.common.nio.NioDataReader;
import com.qipeng.qrpc.common.nio.event.NioEventLoop;
import com.qipeng.qrpc.common.serialize.RpcPacketSerializer;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.util.Iterator;

@Slf4j
public class NioClientEventLoop extends NioEventLoop {

    public NioClientEventLoop() {
        super();
    }

    public static NioClientEventLoop[] group() {
        int n = Runtime.getRuntime().availableProcessors();
        return group(n);
    }

    public static NioClientEventLoop[] group(int size) {
        size = size > 0 ? size : Runtime.getRuntime().availableProcessors();
        NioClientEventLoop[] arr = new NioClientEventLoop[size];
        for (int i = 0; i < size; i++) {
            arr[i] = new NioClientEventLoop();
        }
        return arr;
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
    public void select() {
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
            log.error("NioRpcClient listen 发生异常", e);
        }
    }
}
