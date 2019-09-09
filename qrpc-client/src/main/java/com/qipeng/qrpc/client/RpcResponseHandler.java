package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    public static final RpcResponseHandler INSTANCE = new RpcResponseHandler();

    private RpcResponseHandler() {
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
