package com.qipeng.qrpc.client.handler;

import java.util.ArrayList;
import java.util.List;

public class InvocationHandlerChain {

    private static final List<InvocationHandler> handlerList = new ArrayList<>();

    static {
        handlerList.add(new MonitorHandler());
        handlerList.add(new ServiceDiscoveryHandler());
        handlerList.add(new LoadBalanceHandler());
        handlerList.add(new RpcInvokeHandler());
        buildHandlerChain();
    }

    public static Object invoke(InvocationContext context) {
        handlerList.get(0).invoke(context);
        return context.getResult();
    }

    /**
     * 构造调用链
     */
    private static void buildHandlerChain() {
        InvocationHandler next = null;
        for (int i = handlerList.size() - 1; i >= 0; i--) {
            InvocationHandler handler = handlerList.get(i);
            handler.setNext(next);
            next = handler;
        }
    }
}
