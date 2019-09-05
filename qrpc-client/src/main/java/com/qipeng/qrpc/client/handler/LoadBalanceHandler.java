package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalanceHandler extends AbstractInvocationHandler {

    @Override
    public Object invoke(InvocationContext context) {
        Registry registry = RegistryFactory.getRegistry();
        String serviceName = context.getInvokerParam().getClazz().getName();
        List<ServerParam> serverParams = registry.getServerParam(serviceName);
        ServerParam serverParam = loadBalance(serverParams);
        context.setServerParam(serverParam);
        return getNext().invoke(context);
    }

    private ServerParam loadBalance(List<ServerParam> serverParams) {
        int index = ThreadLocalRandom.current().nextInt(serverParams.size());
        return serverParams.get(index);
    }
}
