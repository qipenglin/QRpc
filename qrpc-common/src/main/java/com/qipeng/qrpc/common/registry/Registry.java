package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.model.ServerInfo;

import java.util.List;

public interface Registry {

    /**
     * 获取服务的提供者列表
     *
     * @param serviceName 服务名称
     * @return 提供者服务器列表
     */
    List<ServerInfo> getServerParam(String serviceName);

    /**
     * 向注册中心注册服务提供者
     *
     * @param serviceName 服务名称
     * @param serverInfo 服务器参数
     * @return 是否注册成功
     */
    boolean registerService(String serviceName, ServerInfo serverInfo);

}
