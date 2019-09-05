package com.qipeng.qrpc.client.handler;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractInvocationHandler implements InvocationHandler {

    @Setter
    @Getter
    private InvocationHandler next;

}
