package com.qipeng.qprc.server;

import com.qipeng.qrpc.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    private RpcRequestHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {

    }
}
