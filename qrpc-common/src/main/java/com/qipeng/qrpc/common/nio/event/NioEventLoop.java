package com.qipeng.qrpc.common.nio.event;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public abstract class NioEventLoop {

    private volatile boolean started;

    private final Executor executor;

    protected Selector selector;

    private final Queue<Runnable> taskQueue;

    public NioEventLoop() {
        this.executor = new ThreadPerTaskExecutor();
        try {
            selector = Selector.open();
        } catch (Exception e) {
            throw new RuntimeException("selector open 失败", e);
        }
        taskQueue = new LinkedBlockingDeque<>();
    }

    public void execute(Runnable runnable) {
        taskQueue.add(runnable);
        if (!started) {
            startThread();
        }
    }

    private synchronized void startThread() {
        if (!started) {
            started = true;
            executor.execute(this::run);
        }
    }

    public void run() {
        while (started) {
            try {
                while (!taskQueue.isEmpty()) {
                    taskQueue.poll().run();
                }
                select();
            } catch (Throwable e) {
                log.error("NioEventLoop run error", e);
            }
        }
    }

    protected abstract void select();

    public void register(SelectableChannel channel, int ops) {
        this.execute(() -> {
            try {
                channel.register(selector, ops);
            } catch (Exception e) {
                log.error("NioEventLoop register 发生异常", e);
            }
        });
    }
}
