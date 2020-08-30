package com.qipeng.qrpc.client.netty;

import com.qipeng.qrpc.common.model.RpcHeartBeat;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author qipenglin
 * @date 2019-09-10 11:46
 **/
@ChannelHandler.Sharable
public class NettyClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                RpcHeartBeat heartBeat = new RpcHeartBeat();
                ctx.channel().writeAndFlush(heartBeat);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
