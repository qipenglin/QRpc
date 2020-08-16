package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.RpcConfig;
import com.qipeng.qrpc.common.registry.impl.RedisRegistry;
import com.qipeng.qrpc.common.registry.impl.ZookeeperRegistry;

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

    public static RegistryConfig getDefaultConfig() {

    }

}
