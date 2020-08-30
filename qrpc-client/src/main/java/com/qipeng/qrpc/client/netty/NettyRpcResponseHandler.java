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
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {

        RpcFuture rpcFuture = RpcFuture.futureMap.get(rpcResponse.getRequestId());
        if (rpcFuture != null) {
            rpcFuture.setResponse(rpcResponse);
            rpcFuture.getLatch().countDown();
        }
    }
}
