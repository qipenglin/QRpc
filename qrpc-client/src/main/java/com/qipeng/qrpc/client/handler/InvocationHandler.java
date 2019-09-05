package com.qipeng.qrpc.client.handler;

public interface InvocationHandler {
    Object invoke(InvocationContext context);

    InvocationHandler getNext();

    void setNext(InvocationHandler handler);
}
