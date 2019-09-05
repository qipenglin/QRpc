package com.qipeng.qrpc.client;

import com.qipeng.qrpc.common.ServerParam;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qipeng
 */
public class RpcClientFactory {

    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static ConcurrentHashMap<ServerParam, RpcClient> clientMap = new ConcurrentHashMap<ServerParam, RpcClient>();

    public static RpcClient getClient(ServerParam serverParam) {
        RpcClient client = clientMap.get(serverParam);
        if (client != null) {
            return client;
        }
        synchronized (RpcClientFactory.class) {
            if (clientMap.get(serverParam) == null) {
                client = new RpcClient();
                client.activate(workerGroup, serverParam);
                clientMap.put(serverParam, client);
                return client;
            }
        }
        return clientMap.get(serverParam);
    }
}
