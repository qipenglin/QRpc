package com.qipeng.qrpc.client.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonitorHandler extends AbstractInvocationHandler {

    @Override
    void doInvoke(InvocationContext context) {
        log.info("Rpc Invoke,param:{}", context.getRpcRequest());
    }
}
