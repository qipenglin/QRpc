package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.model.ServerInfo;

import java.util.List;

public interface Registry {

    /**
     * 获取服务的提供者列表
     *
     * @param serviceName
     * @return
     */
    List<ServerInfo> getServerParam(String serviceName);

    /**
     * 向注册中心注册服务提供者
     *
     * @param serviceName
     * @param serverInfo
     * @return
     */
    boolean registerService(String serviceName, ServerInfo serverInfo);

}
