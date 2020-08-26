package com.qipeng.qrpc.server;/**
 * @author qipenglin
 * @date 2019-09-10 11:36
 **/

import com.qipeng.qrpc.common.RpcHeartBeat;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qipenglin
 * @date 2019-09-10 11:36
 **/
@Slf4j
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<RpcHeartBeat> {

    private Map<Channel, AtomicInteger> lossMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcHeartBeat msg) {
        log.info("Receive RpcHeartBeat from :{}", ctx.channel().localAddress().toString());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                int loss = lossMap.computeIfAbsent(ctx.channel(), ch -> new AtomicInteger(0)).addAndGet(1);
                if (loss > 3) {
                    lossMap.remove(ctx.channel());
                    ctx.channel().close();
                    log.info("close inactive channel with:{}", ctx.channel().localAddress());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
