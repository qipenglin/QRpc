package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.ServerInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qipeng
 */
public class RpcClientFactory {

    private static ConcurrentHashMap<ServerInfo, RpcClient> clientMap = new ConcurrentHashMap<>();

    public static RpcClient getClient(ServerInfo serverInfo) {
        RpcClient client = clientMap.get(serverInfo);
        if (client != null) {
            return client;
        }
        synchronized (RpcClientFactory.class) {
            if (clientMap.get(serverInfo) == null) {
                client = new NettyRpcClient(serverInfo);
                clientMap.put(serverInfo, client);
                return client;
            }
        }
        return clientMap.get(serverInfo);
    }



}
