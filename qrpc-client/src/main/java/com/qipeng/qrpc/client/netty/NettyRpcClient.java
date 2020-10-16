package com.qipeng.qrpc.client.netty;

import com.qipeng.qrpc.client.AbstractRpcClient;
import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.netty.codec.PacketCodecHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClient extends AbstractRpcClient {
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServerInfo serverInfo;
    private Bootstrap bootstrap;
    private Channel channel;

    public NettyRpcClient(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        initBootstrap();
        connect(serverInfo);
    }

    private void initBootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel channel) {
                         channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4))
                                .addLast(new PacketCodecHandler())
                                .addLast(new NettyRpcResponseHandler())
                                .addLast(new IdleStateHandler(0, 60, 0))
                                .addLast(new NettyClientHeartBeatTrigger());
                     }

                     @Override
                     public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                         super.channelInactive(ctx);
                         doConnect(serverInfo);
                     }
                 });
    }

    protected void doConnect(ServerInfo serverInfo) {
        try {
            InetSocketAddress remoteAddress = new InetSocketAddress(serverInfo.getHost(),
                                                                    serverInfo.getPort());
            ChannelFuture channelFuture = bootstrap.connect(remoteAddress).sync();
            channel = channelFuture.channel();
            setConnected(true);
            log.info("NettyRpcClient连接成功，serverInfo: {}", serverInfo);
        } catch (InterruptedException e) {
            throw new RpcException("netty客户端连接服务器失败，serverParam:" + serverInfo, e);
        }
    }

    public RpcResponse invokeRpc(RpcRequest request, int timeout) {
        RpcFuture future = new RpcFuture(request.getRequestId(), timeout);
        if (!isConnected() || channel == null || !channel.isActive()) {
            doConnect(serverInfo);
        }
        channel.writeAndFlush(request);
        return future.get();
    }
}

