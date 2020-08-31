package com.qipeng.qrpc.common.nio;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class NioDataCache {

    //消息暂存队列，最大只能存储size条消息
    private Queue<byte[]> queue;

    @Getter
    private ByteBuffer byteBuffer;

    public NioDataCache(int size) {
        queue = new ArrayDeque<>(size);
        byteBuffer = ByteBuffer.allocate(NioDataReader.lengthOfData * 10);
    }

    public boolean isReady() {
        return !queue.isEmpty();
    }

    public byte[] getData() {
        return queue.poll();
    }
}
