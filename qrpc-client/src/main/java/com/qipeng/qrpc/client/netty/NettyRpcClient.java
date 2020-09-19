package com.qipeng.qrpc.client.netty;

import com.qipeng.qrpc.client.AbstractRpcClient;
import com.qipeng.qrpc.client.RpcClient;
import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.netty.codec.PacketCodecHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient extends AbstractRpcClient {
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    @Getter
    private final ServerInfo serverInfo;
    private Bootstrap bootstrap;
    private Channel channel;
    private boolean isConnected;

    public NettyRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        initBootstrap();
        connect(serverInfo);
    }

    private void initBootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 获取channel中的pipeline
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4));
                pipeline.addLast(new PacketCodecHandler());
                pipeline.addLast(new NettyRpcResponseHandler());
                pipeline.addLast(new IdleStateHandler(0, 4, 0));
                pipeline.addLast(new NettyClientHeartBeatHandler());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                connect(serverInfo);
            }
        });
    }

    public RpcResponse invokeRpc(RpcRequest request) {
        channel.writeAndFlush(request);
        RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
        return rpcFuture.get();
    }

    protected void doConnect(ServerInfo serverInfo) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort())).sync();
            channelFuture.addListener(f -> {
                isConnected = true;
                log.info("启动netty客户端成功，serverInfo: {}", serverInfo);
            });
            channel = channelFuture.channel();
        } catch (InterruptedException e) {
            log.error("netty客户端连接服务器失败，serverParam: {}", serverInfo, e);
        }
    }
}

