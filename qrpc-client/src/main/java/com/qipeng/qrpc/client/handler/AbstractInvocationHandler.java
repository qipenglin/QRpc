package com.qipeng.qrpc.client.handler;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractInvocationHandler implements InvocationHandler {

    @Setter
    @Getter
    private InvocationHandler next;

    @Override
    public void invoke(InvocationContext context) {
        doInvoke(context);
        if (getNext() != null) {
            getNext().invoke(context);
        }
    }

    abstract void doInvoke(InvocationContext context);
}
