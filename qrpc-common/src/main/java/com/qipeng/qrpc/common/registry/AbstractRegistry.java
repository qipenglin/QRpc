package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.model.ServerInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractRegistry implements Registry {

    @Getter
    protected final Map<String, List<ServerInfo>> serviceMap = new ConcurrentHashMap<>();

    @Override
    public List<ServerInfo> getServerInfo(String serviceName) {
        List<ServerInfo> serverInfos = serviceMap.get(serviceName);
        if (serverInfos != null) {
            return serverInfos;
        }
        synchronized (serviceMap) {
            try {
                serverInfos = serviceMap.computeIfAbsent(serviceName, this::doGetServerParam);
                subscribe(serviceName);
                return serverInfos;
            } catch (Exception e) {
                log.error("从注册中心获取服务器列表出现异常", e);
                return Collections.emptyList();
            }
        }
    }

    public abstract List<ServerInfo> doGetServerParam(String serviceName);
}
