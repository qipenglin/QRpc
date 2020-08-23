package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.ServerInfo;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 负载均衡服务器，从服务器列表中选取一个作为最终执行的
 */
public class LoadBalanceHandler extends AbstractInvocationHandler {

    @Override
    public void doInvoke(InvocationContext context) {
        ServerInfo serverInfo = loadBalance(context.getServerInfos());
        context.setServerInfo(serverInfo);
    }

    private ServerInfo loadBalance(List<ServerInfo> serverInfos) {
        if (serverInfos.size() == 1) {
            return serverInfos.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(serverInfos.size());
        return serverInfos.get(index);
    }
}
