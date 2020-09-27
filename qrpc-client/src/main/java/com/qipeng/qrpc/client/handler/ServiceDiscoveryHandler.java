package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import com.qipeng.qrpc.common.registry.RegistryFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 服务发现处理器,从注册中心获取服务器列表
 */
public class ServiceDiscoveryHandler extends AbstractInvocationHandler {

    @Override
    void doInvoke(InvocationContext context) {
        RegistryConfig registryConfig = context.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig);
        String serviceName = context.getRpcRequest().getClazz().getName();
        List<ServerInfo> serverInfos = registry.getServerInfo(serviceName);
        if (CollectionUtils.isEmpty(serverInfos)) {
            throw new RpcException("Service" + serviceName + " Not Found");
        }
        context.setServerInfos(serverInfos);
    }
}
