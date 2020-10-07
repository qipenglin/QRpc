package com.qipeng.qrpc.common.nio.event;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class NioEventLoopGroup {

    @Getter
    public NioEventLoop[] eventLoops;

    private final AtomicInteger idx = new AtomicInteger();

    public NioEventLoop next() {
        return eventLoops[idx.getAndIncrement() % eventLoops.length];
    }

    public NioEventLoopGroup(NioEventLoop[] eventLoops) {
        this.eventLoops = eventLoops;
    }
}
