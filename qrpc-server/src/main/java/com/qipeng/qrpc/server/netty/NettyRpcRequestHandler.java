package com.qipeng.qrpc.server.netty;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.server.RpcInvoker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final ThreadPoolExecutor invokeExecutor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("NettyRequestHandler-%d").build();
        invokeExecutor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                                                new ArrayBlockingQueue<>(10000), threadFactory);
    }

    public NettyRpcRequestHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        invokeExecutor.submit(() -> {
            RpcResponse response = RpcInvoker.invoke(request);
            ctx.channel().writeAndFlush(response);
        });
    }
}
