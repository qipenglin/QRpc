package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcFuture {

    private static final int DEFAULT_TIMEOUT = 30000;

    public static Map<Integer, RpcFuture> futureMap = new ConcurrentHashMap<>();

    @Getter
    private final CountDownLatch latch = new CountDownLatch(1);

    @Getter
    private final Integer requestId;

    private final int timeout;

    private final Long start;

    @Setter
    private volatile RpcResponse response;

    public RpcFuture(Integer requestId, int timeout) {
        this.requestId = requestId;
        this.start = System.currentTimeMillis();
        this.timeout = timeout <= 0 ? DEFAULT_TIMEOUT : timeout;
        futureMap.put(requestId, this);
    }

    public RpcResponse get() {
        while (!isTimeout() && response == null) {
            try {
                long remainTime = timeout - (System.currentTimeMillis() - start);
                latch.await(remainTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.warn("rpc thread is interrupted,requestId:{}", requestId, e);
            }
        }
        futureMap.remove(requestId);
        if (response == null) {
            throw new RpcException("rpc time out");
        }
        return response;
    }

    private boolean isTimeout() {
        return System.currentTimeMillis() - start > timeout;
    }

}
