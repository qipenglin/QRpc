package com.qipeng.qrpc.client.handler;

public interface InvocationHandler {

    void invoke(InvocationContext context);

    InvocationHandler getNext();

    void setNext(InvocationHandler handler);
}
