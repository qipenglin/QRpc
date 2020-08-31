package com.qipeng.qrpc.common.nio;

import com.qipeng.qrpc.common.exception.RpcException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NioDataReader {

    public static final int lengthOfData = 400;

    public static void readData(SelectionKey sk) {
        SocketChannel channel = (SocketChannel) sk.channel();
        NioDataCache nioDataCache = (NioDataCache) sk.attachment();
        if (nioDataCache == null) {
            sk.attach(nioDataCache = new NioDataCache(1000));
        }
        ByteBuffer byteBuffer = nioDataCache.byteBuffer;
        try {
            //一口气读完全部数据
            while (channel.read(byteBuffer) > 0) ;
        } catch (IOException e) {
            throw new RpcException("NIO从channel读取数据失败", e);
        }
        byteBuffer.flip();
        while (byteBuffer.remaining() >= 7) {
            int len = byteBuffer.getInt(byteBuffer.position() + 3);
            if (byteBuffer.remaining() >= len) {
                byte[] data = new byte[len + 3];
                byteBuffer.get(data);
                nioDataCache.queue.add(data);
            } else {
                break;
            }
        }

        //把可能的半包移动到前面，待下一次处理
        for (int i = 0; i < byteBuffer.remaining(); i++) {
            byteBuffer.put(i, byteBuffer.get(byteBuffer.position() + i));
        }
        byteBuffer.position(byteBuffer.remaining());
        byteBuffer.limit(byteBuffer.capacity());
    }
}
