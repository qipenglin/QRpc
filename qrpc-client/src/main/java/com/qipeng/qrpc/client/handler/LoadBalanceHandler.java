package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.ServerParam;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalanceHandler extends AbstractInvocationHandler {

    @Override
    public Object doInvoke(InvocationContext context) {
        ServerParam serverParam = loadBalance(context.getServerParams());
        context.setServerParam(serverParam);
        return null;
    }

    private ServerParam loadBalance(List<ServerParam> serverParams) {
        if (serverParams.size() == 1) {
            return serverParams.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(serverParams.size());
        return serverParams.get(index);
    }
}
