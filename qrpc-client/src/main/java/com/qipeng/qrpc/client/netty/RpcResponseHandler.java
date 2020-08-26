package com.qipeng.qrpc.client.netty;

import com.qipeng.qrpc.client.RpcFuture;
import com.qipeng.qrpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
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
