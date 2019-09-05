package com.qipeng.qrpc.client.handler;

import java.util.ArrayList;
import java.util.List;

public class InvocationHandlerChain {

    private static InvocationHandler firstHandler;

    private static List<InvocationHandler> handlerList = new ArrayList<>();

    static {
        handlerList.add(new LoadBalanceHandler());
        handlerList.add(new RpcInvokeHandler());
        firstHandler = buildHandlerChain();
    }

    public static Object invoke(InvocationContext context) {
        return firstHandler.invoke(context);
    }

    private static InvocationHandler buildHandlerChain() {
        InvocationHandler next = null;
        for (int i = handlerList.size() - 1; i >= 0; i--) {
            InvocationHandler handler = handlerList.get(i);
            handler.setNext(next);
            next = handler;
        }
        return next;
    }

}
