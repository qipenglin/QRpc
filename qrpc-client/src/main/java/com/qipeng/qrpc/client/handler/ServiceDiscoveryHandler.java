package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.registry.Registry;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 服务发现处理器,从注册中心获取服务器列表
 */
public class ServiceDiscoveryHandler extends AbstractInvocationHandler {

    @Override
    void doInvoke(InvocationContext context) {
        Registry registry = context.getRegistry();
        String serviceName = context.getInvokeParam().getClazz().getName();
        List<ServerInfo> serverInfos = registry.getServerParam(serviceName);
        if (CollectionUtils.isEmpty(serverInfos)) {
            throw new RpcException("Service" + serviceName + " Not Found");
        }
        context.setServerInfos(serverInfos);
    }
}
