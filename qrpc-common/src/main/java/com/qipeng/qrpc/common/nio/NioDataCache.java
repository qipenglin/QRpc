package com.qipeng.qrpc.common.nio;

import lombok.Data;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

@Data
public class NioDataCache {

    //消息暂存队列，最大只能存储size条消息
    private Queue<byte[]> queue;

    @Getter
    private ByteBuffer buffer;

    public NioDataCache(int size) {
        queue = new ArrayDeque<>(size);
        buffer = ByteBuffer.allocate(NioDataReader.lengthOfData * 10);
    }

    public boolean isReady() {
        return !queue.isEmpty();
    }

    public byte[] getData() {
        return queue.poll();
    }

    public void add(byte[] bytes) {
        queue.add(bytes);
    }
}
