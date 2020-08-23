package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.ServerParam;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡服务器，从服务器列表中选取一个作为最终执行的
 */
public class LoadBalanceHandler extends AbstractInvocationHandler {

    @Override
    public void doInvoke(InvocationContext context) {
        ServerParam serverParam = loadBalance(context.getServerParams());
        context.setServerParam(serverParam);
    }

    private ServerParam loadBalance(List<ServerParam> serverParams) {
        if (serverParams.size() == 1) {
            return serverParams.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(serverParams.size());
        return serverParams.get(index);
    }
}
