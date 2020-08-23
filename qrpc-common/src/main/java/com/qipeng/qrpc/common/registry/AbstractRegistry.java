package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegistry implements Registry {

    private final Map<String, List<ServerInfo>> serviceMap = new ConcurrentHashMap<>();

    @Override
    public List<ServerInfo> getServerParam(String serviceName) {
        List<ServerInfo> serverInfos = serviceMap.get(serviceName);
        if (serverInfos != null) {
            return serverInfos;
        }
        try {
            List<ServerInfo> res = doGetServerParam(serviceName);
            subscribe(serviceName);
            return res;
        } catch (Exception e) {
            throw new RpcException();
        }
    }

    protected abstract void subscribe(String serviceName);

    public abstract List<ServerInfo> doGetServerParam(String serviceName);
}
