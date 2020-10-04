package com.qipeng.qrpc.client.netty;

import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.model.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class NettyRpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    public NettyRpcResponseHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        RpcFuture future = RpcFuture.getFuture(response.getRequestId());
        if (future != null) {
            future.setResponse(response);
            future.getLatch().countDown();
        }
    }
}
