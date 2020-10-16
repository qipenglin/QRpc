package com.qipeng.qrpc.common.nio;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class NioDataCache {
    //buffer的默认空间，可改成可配置的
    private static final int CACHE_SIZE = 1024 * 1024;
    @Getter
    private final ByteBuffer buffer;
    //消息暂存队列
    private final Queue<byte[]> queue;
    //capacity,queue最大只能缓存capacity个包
    private final int capacity;

    public NioDataCache(int capacity) {
        queue = new LinkedBlockingDeque<>(capacity);
        this.capacity = capacity;
        buffer = ByteBuffer.allocate(CACHE_SIZE);
    }

    public boolean isReady() {
        return !queue.isEmpty();
    }

    public boolean isFull() {
        return queue.size() >= capacity;
    }

    public byte[] getData() {
        try {
            return ((LinkedBlockingDeque<byte[]>) queue).poll(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void add(byte[] bytes) {
        queue.add(bytes);
    }
}
