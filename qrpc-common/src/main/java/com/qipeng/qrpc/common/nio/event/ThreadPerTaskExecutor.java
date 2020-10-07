package com.qipeng.qrpc.common.nio.event;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class ThreadPerTaskExecutor implements Executor {

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor() {
        threadFactory = new BasicThreadFactory.Builder().namingPattern("ThreadPerTaskExecutor-%d").build();
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
