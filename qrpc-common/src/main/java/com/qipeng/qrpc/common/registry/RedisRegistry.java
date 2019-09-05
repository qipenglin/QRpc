package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.ServerParam;

import java.util.List;

public class RedisRegistry implements Registry {

    private volatile static RedisRegistry instance;

    public static RedisRegistry getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ZookeeperRegistry.class) {
            if (instance == null) {
                instance = new RedisRegistry();
            }
            return instance;
        }
    }

    @Override
    public List<ServerParam> getServerParam(String serviceName) {
        return null;
    }

    @Override
    public boolean registerService(String serviceName, ServerParam serverParam) {
        return false;
    }
}
