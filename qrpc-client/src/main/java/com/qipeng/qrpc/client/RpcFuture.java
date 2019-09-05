package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture {

    private static final int timeout = 30000;

    static Map<Integer, RpcFuture> futureMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    @Getter
    private Condition condition = lock.newCondition();

    @Getter
    private Integer requestId;

    private Long invokeTime;

    @Setter
    private RpcResponse response;

    RpcFuture(Integer requestId) {
        this.requestId = requestId;
        this.invokeTime = System.currentTimeMillis();
        futureMap.put(requestId, this);
    }

    RpcResponse get() {
        try {
            lock.lock();
            while (!isTimeout() && response == null) {
                condition.await(timeout, TimeUnit.MILLISECONDS);
            }
            futureMap.remove(requestId);
            return response;
        } catch (InterruptedException e) {
            return response;
        } finally {
            lock.unlock();
        }
    }

    private boolean isTimeout() {
        return System.currentTimeMillis() - invokeTime > timeout;
    }

}
