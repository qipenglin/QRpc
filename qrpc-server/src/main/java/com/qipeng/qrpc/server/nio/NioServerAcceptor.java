package com.qipeng.qrpc.server.nio;

import com.qipeng.qrpc.common.nio.event.NioEventLoop;
import com.qipeng.qrpc.common.nio.event.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class NioServerAcceptor extends NioEventLoop {

    private final ServerSocketChannel serverChannel;

    private final NioEventLoopGroup workGroup;

    public NioServerAcceptor(ServerSocketChannel serverChannel) {
        super();
        this.serverChannel = serverChannel;
        this.workGroup = new NioEventLoopGroup(NioServerEventLoop.group());
    }

    @Override
    protected void select() {
        try {
            selector.select(100);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                iterator.remove();
                if (sk.isAcceptable()) {
                    execute(this::accept);
                }
            }
        } catch (Exception e) {
            log.error("NioServerAcceptor select exception", e);
        }
    }

    private void accept() {
        try {
            SocketChannel sc = serverChannel.accept();
            sc.configureBlocking(false);
            workGroup.next().register(sc, SelectionKey.OP_READ);
            log.info("NioServerAcceptor收到客户端连接请求,remoteAddress:{}", sc.getRemoteAddress());
        } catch (Exception e) {
            log.error("NioServerAcceptor accept 发生异常", e);
        }
    }
}
