package com.qipeng.qrpc.client.handler;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractInvocationHandler implements InvocationHandler {

    @Setter
    @Getter
    private InvocationHandler next;

    @Override
    public Object invoke(InvocationContext context) {
        Object o = doInvoke(context);
        return getNext() == null ? o : getNext().invoke(context);
    }

    abstract Object doInvoke(InvocationContext context);
}
