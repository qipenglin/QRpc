package com.qipeng.qrpc.client;

import com.qipeng.qrpc.client.proxy.ProxyFactory;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.registry.Registry;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qipeng
 */
public class RpcClientFactory {

    private static ConcurrentHashMap<ServerParam, RpcClient> clientMap = new ConcurrentHashMap<>();

    public static RpcClient getClient(ServerParam serverParam) {
        RpcClient client = clientMap.get(serverParam);
        if (client != null) {
            return client;
        }
        synchronized (RpcClientFactory.class) {
            if (clientMap.get(serverParam) == null) {
                client = new RpcClient(serverParam);
                clientMap.put(serverParam, client);
                return client;
            }
        }
        return clientMap.get(serverParam);
    }



}
