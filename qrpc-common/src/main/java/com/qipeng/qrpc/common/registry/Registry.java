package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.ServerParam;

import java.util.List;

public interface Registry {

    /**
     * 获取服务的提供者列表
     *
     * @param serviceName
     * @return
     */
    List<ServerParam> getServerParam(String serviceName);

    /**
     * 向注册中心注册服务提供者
     *
     * @param serviceName
     * @param serverParam
     * @return
     */
    boolean registerService(String serviceName, ServerParam serverParam);

}
