package com.qipeng.qrpc.common.nio;

import com.qipeng.qrpc.common.exception.RpcException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NioDataReader {

    public static final int lengthOfData = 40000;

    public static void readData(SelectionKey sk) {
        SocketChannel channel = (SocketChannel) sk.channel();
        NioDataCache nioDataCache = (NioDataCache) sk.attachment();
        if (nioDataCache == null) {
            sk.attach(nioDataCache = new NioDataCache(1000));
        }
        ByteBuffer buffer = nioDataCache.getBuffer();
        try {
            //一口气读完全部数据
            while (channel.read(buffer) > 0) ;
        } catch (IOException e) {
            IOUtils.closeQuietly(channel, null);
            throw new RpcException("NIO从channel读取数据失败", e);
        }
        buffer.flip();
        while (buffer.remaining() >= 7) {
            int len = buffer.getInt(buffer.position() + 3);
            if (buffer.remaining() >= len) {
                byte[] data = new byte[len + 3];
                buffer.get(data);
                nioDataCache.add(data);
            } else {
                break;
            }
        }

        //把可能的半包移动到前面，待下一次处理
        for (int i = 0; i < buffer.remaining(); i++) {
            buffer.put(i, buffer.get(buffer.position() + i));
        }
        buffer.position(buffer.remaining());
        buffer.limit(buffer.capacity());
    }
}
