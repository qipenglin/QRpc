package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.registry.impl.RedisRegistry;
import com.qipeng.qrpc.common.registry.impl.ZookeeperRegistry;

public class RegistryFactory {

    public static Registry getDefaultRegistry() {
        String uri = RpcConfig.REGISTRY_URI;
        RegistryConfig config = buildRegistryConfig(uri);
        return getRegistry(config);
    }

    public static Registry getRegistry(String uri) {
        RegistryConfig config = buildRegistryConfig(uri);
        return getRegistry(config);
    }

    private static Registry getRegistry(RegistryConfig config) {
        switch (config.getProtocol()) {
            case REDIS:
                return RedisRegistry.getInstance(config);
            case ZOOKEEPER:
                return ZookeeperRegistry.getInstance(config);
            default:
                throw new RpcException("暂不支持该注册中心协议");
        }
    }

    private static RegistryConfig buildRegistryConfig(String uri) {
        RegistryProtocol protocol = RegistryProtocol.forName(uri.substring(uri.indexOf("/")));
        String[] words = uri.split(":");
        String host = words[1];
        int port = Integer.parseInt(words[2]);
        return new RegistryConfig(protocol, host, port);
    }

}
