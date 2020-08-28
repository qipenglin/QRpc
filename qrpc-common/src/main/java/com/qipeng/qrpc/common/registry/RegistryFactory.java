package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.registry.impl.RedisRegistry;
import com.qipeng.qrpc.common.registry.impl.ZookeeperRegistry;

public class RegistryFactory {

    public static Registry getDefaultRegistry() {
        RegistryConfig config = getDefaultRegistryConfig();
        return getRegistry(config);
    }

    public static Registry getRegistry(RegistryConfig config) {
        if (config == null) {
            config = getDefaultRegistryConfig();
        }
        switch (config.getProtocol()) {
            case REDIS:
                return RedisRegistry.getInstance(config);
            case ZOOKEEPER:
                return ZookeeperRegistry.getInstance(config);
            default:
                throw new RpcException("暂不支持该注册中心协议");
        }
    }

    public static RegistryConfig getDefaultRegistryConfig() {
        return buildRegistryConfig(RpcConfig.REGISTRY);
    }

    public static RegistryConfig buildRegistryConfig(String uri) {
        String[] words = uri.split("://");
        RegistryProtocol protocol = RegistryProtocol.forName(words[0]);
        return new RegistryConfig(protocol, words[1]);
    }

}
