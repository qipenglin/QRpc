package com.qipeng.qrpc.client;/**
 * @Author qipenglin
 * @Date 2019-09-10 11:46
 **/

import com.qipeng.qrpc.common.RpcHeartBeat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Author qipenglin
 * @Date 2019-09-10 11:46
 **/
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                RpcHeartBeat heartBeat = new RpcHeartBeat();
                ctx.writeAndFlush(heartBeat);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
