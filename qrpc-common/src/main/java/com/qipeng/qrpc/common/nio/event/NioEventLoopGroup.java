package com.qipeng.qrpc.common.nio.event;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class NioEventLoopGroup {

    private final AtomicInteger idx = new AtomicInteger();
    @Getter
    public NioEventLoop[] eventLoops;

    public NioEventLoopGroup(NioEventLoop[] eventLoops) {
        this.eventLoops = eventLoops;
    }

    public NioEventLoop next() {
        return eventLoops[idx.getAndIncrement() % eventLoops.length];
    }
}
