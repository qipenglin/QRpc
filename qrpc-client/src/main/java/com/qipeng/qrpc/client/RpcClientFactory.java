package com.qipeng.qrpc.client;

import com.qipeng.qrpc.client.bio.BioRpcClient;
import com.qipeng.qrpc.client.netty.NettyRpcClient;
import com.qipeng.qrpc.common.model.NetworkModel;
import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.model.ServerInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qipeng
 */
public class RpcClientFactory {

    private final static ConcurrentHashMap<ServerInfo, RpcClient> clientMap = new ConcurrentHashMap<>();

    public static RpcClient getClient(ServerInfo serverInfo) {
        RpcClient client = clientMap.get(serverInfo);
        if (client != null) {
            return client;
        }
        synchronized (RpcClientFactory.class) {
            if (clientMap.get(serverInfo) == null) {
                NetworkModel networkModel = NetworkModel.getByName(RpcConfig.NETWORK_MODEL);
                switch (networkModel) {
                    case BIO:
                        client = new BioRpcClient(serverInfo);
                        break;
                    case NETTY:
                    default:
                        client = new NettyRpcClient(serverInfo);
                }
                clientMap.put(serverInfo, client);
                return client;
            }
        }
        return clientMap.get(serverInfo);
    }


}
