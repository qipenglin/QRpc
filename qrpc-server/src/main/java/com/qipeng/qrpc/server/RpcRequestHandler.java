package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.RpcRequest;
import com.qipeng.qrpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    private static ThreadPoolExecutor executor;

    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("requestDealer-{}").build();
        executor = new ThreadPoolExecutor(3, 10, 1000L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000), threadFactory);
    }

    private RpcRequestHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        executor.submit(() -> {
            RpcResponse response = RpcInvoker.invoke(request);
            ctx.channel().writeAndFlush(response);
        });
    }
}
