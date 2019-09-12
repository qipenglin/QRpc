package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.RpcConfig;

public class RegistryFactory {

    public static Registry getRegistryFromConfig() {
        switch (RpcConfig.REGISTRY_PROTOCOL) {
            case RegistryProtocol.REDIS:
                return RedisRegistry.getInstance();
            case RegistryProtocol.ZOOKEEPER:
            default:
                return ZookeeperRegistry.getInstance();
        }
    }

}
