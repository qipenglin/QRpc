package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.exception.RpcException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRegistry implements Registry {

    private final Map<String, List<ServerParam>> serviceMap = new ConcurrentHashMap<>();

    @Override
    public List<ServerParam> getServerParam(String serviceName) {
        List<ServerParam> serverParams = serviceMap.get(serviceName);
        if (serverParams != null) {
            return serverParams;
        }
        try {
            List<ServerParam> res = doGetServerParam(serviceName);
            subscribe(serviceName);
            return res;
        } catch (Exception e) {
            throw new RpcException();
        }
    }

    protected abstract void subscribe(String serviceName);

    abstract List<ServerParam> doGetServerParam(String serviceName);
}
