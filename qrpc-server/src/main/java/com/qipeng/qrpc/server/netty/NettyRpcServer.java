package com.qipeng.qrpc.server.netty;

import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.netty.codec.PacketCodecHandler;
import com.qipeng.qrpc.server.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {
    /**
     * 服务端是否已经启动
     */
    private static NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private volatile static NettyRpcServer instance;
    private volatile boolean isActivated;

    private NettyRpcServer() {
    }

    public static NettyRpcServer getInstance() {
        if (instance == null) {
            synchronized (NettyRpcServer.class) {
                if (instance == null) {
                    instance = new NettyRpcServer();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void start(ServerInfo serverInfo) {
        if (isActivated) {
            return;
        }
        log.info("尝试启动server:{}", serverInfo);
        ServerBootstrap bootstrap = initServerBootstrap();
        try {
            bootstrap.bind(serverInfo.getPort()).sync();
            log.info("netty启动成功");
            isActivated = true;
        } catch (Exception e) {
            log.error("绑定ip和端口，并启动netty，失败", e);
        }
    }

    private ServerBootstrap initServerBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                 .channel(NioServerSocketChannel.class)
                 // 当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                 .option(ChannelOption.SO_BACKLOG, 1024)
                 .childOption(ChannelOption.SO_KEEPALIVE, true)
                 .childHandler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel channel) throws Exception {
                         ChannelPipeline pipeline = channel.pipeline();
                         pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4));
                         pipeline.addLast(new PacketCodecHandler());
                         pipeline.addLast(new DefaultEventLoopGroup(), new NettyRpcRequestHandler());
                         pipeline.addLast(new IdleStateHandler(31, 0, 0));
                         pipeline.addLast(new NettyServerHeartBeatHandler());
                     }
                 });
        return bootstrap;
    }
}
