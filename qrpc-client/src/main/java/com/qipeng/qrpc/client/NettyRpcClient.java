package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.PacketCodecHandler;
import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.ServerInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient implements RpcClient {

    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    @Getter
    private final ServerInfo serverInfo;

    public RpcResponse invokeRpc(RpcRequest request) {
        channel.writeAndFlush(request);
        RpcFuture rpcFuture = new RpcFuture(request.getRequestId());
        return rpcFuture.get();
    }

    public NettyRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        doConnect(serverInfo);
    }

    private void doConnect(ServerInfo serverInfo) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 获取channel中的pipeline
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4));
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
