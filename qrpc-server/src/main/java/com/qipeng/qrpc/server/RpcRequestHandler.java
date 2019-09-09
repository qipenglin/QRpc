package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public static final RpcRequestHandler INSTANCE = new RpcRequestHandler();

    private RpcRequestHandler() {
        super();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        String serviceName = request.getClazz().getName();
        ServiceProvider provider = RpcServer.PROVIDER_MAP.get(serviceName);
        if (provider == null) {
            throw new RuntimeException("服务不存在");
        }
        RequestDealerFactory.getRequestDealer().dealRequest(ctx.channel(), request, provider);
    }
}
