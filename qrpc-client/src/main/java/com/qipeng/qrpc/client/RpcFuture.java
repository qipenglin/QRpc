package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.RpcResponse;
import com.qipeng.qrpc.common.exception.RpcException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class RpcFuture {

    private static final int timeout = 30000;

    static Map<Integer, RpcFuture> futureMap = new ConcurrentHashMap<>();

    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);

    @Getter
    private final Integer requestId;

    private final Long invokeTime;

    @Setter
    private RpcResponse response;

    RpcFuture(Integer requestId) {
        this.requestId = requestId;
        this.invokeTime = System.currentTimeMillis();
        futureMap.put(requestId, this);
    }

    RpcResponse get() {
        while (!isTimeout() && response == null) {
            try {
                latch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.warn("rpc thread is interrupted,requestId:{}", requestId);
            }
        }
        futureMap.remove(requestId);
        if (response == null) {
            throw new RpcException("time out");
        }
        return response;
    }

    private boolean isTimeout() {
        return System.currentTimeMillis() - invokeTime > timeout;
    }

}
