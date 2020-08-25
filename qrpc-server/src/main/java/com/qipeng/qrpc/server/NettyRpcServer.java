package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.PacketCodecHandler;
import com.qipeng.qrpc.common.ServerInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyRpcServer implements RpcServer {

    private EventLoopGroup boss;

    private EventLoopGroup worker;

    private ServerBootstrap bootstrap;

    /**
     * 服务端是否已经启动
     */
    private volatile boolean isActivated;

    public void start(ServerInfo serverInfo) {
        if (isActivated) {
            return;
        }
        log.info("尝试启动server: {}", serverInfo);

        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        // 将boss组和worker组绑定在Netty上下文里
        bootstrap.group(boss, worker);
        // 设置底层Channel
        bootstrap.channel(NioServerSocketChannel.class);
        // 设置业务层Channel
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4));
                pipeline.addLast(PacketCodecHandler.INSTANCE);
                pipeline.addLast(RpcRequestHandler.INSTANCE);
                pipeline.addLast(new IdleStateHandler(5, 0, 0));
                pipeline.addLast(new HeartBeatServerHandler());
            }
        });
        // 当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        // 绑定ip和端口，并启动netty
        try {
            bootstrap.bind(serverInfo.getHost(), serverInfo.getPort()).sync()
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            log.info("netty启动成功");
                            isActivated = true;
                        }
                    });
        } catch (InterruptedException e) {
            log.error("绑定ip和端口，并启动netty，失败", e);
        }
    }
}
