package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.PacketCodecHandler;
import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.serializer.RpcPacketSplitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class RpcClient {

    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    @Getter
    private ServerParam serverInfo;

    public RpcResponse invokeRpc(RpcRequest request) {
        channel.writeAndFlush(request);
        RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
        return rpcFuture.get();
    }

    RpcClient(ServerParam serverInfo) {
        this.serverInfo = serverInfo;
        doConnect(serverInfo);
    }

    private void doConnect(ServerParam serverInfo) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 获取channel中的pipeline
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new RpcPacketSplitter());
                pipeline.addLast(PacketCodecHandler.INSTANCE);
                pipeline.addLast(RpcResponseHandler.INSTANCE);
                pipeline.addLast(new IdleStateHandler(0, 4, 0));
                pipeline.addLast(new HeartBeatClientHandler());
            }
        });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort())).sync();
        } catch (InterruptedException e) {
            log.error("netty客户端连接服务器失败，serverParam: {}", serverInfo, e);
        }
        if (channelFuture != null) {
            channelFuture.addListener(f -> log.info("启动netty客户端成功，serverInfo: {}", serverInfo));
            channel = channelFuture.channel();
        }
    }
}

