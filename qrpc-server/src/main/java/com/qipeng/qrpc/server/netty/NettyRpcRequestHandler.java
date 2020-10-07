package com.qipeng.qrpc.server.netty;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.server.RpcInvoker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public NettyRpcRequestHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        log.info("NettyRpcServer request:{}", request);
        RpcResponse response = RpcInvoker.invoke(request);
        ctx.channel().writeAndFlush(response);
    }
}
