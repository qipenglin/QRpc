package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.ServerParam;
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
        List<ServerParam> serverParams = registry.getServerParam(serviceName);
        if (CollectionUtils.isEmpty(serverParams)) {
            throw new RpcException("Service" + serviceName + " Not Found");
        }
        context.setServerParams(serverParams);
    }
}
