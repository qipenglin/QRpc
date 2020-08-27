package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.model.InvokerParam;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import lombok.Data;

import java.util.List;

@Data
public class InvocationContext {

    /**
     * 注册中心
     */
    private RegistryConfig registryConfig;
    /**
     * 可供选择的服务器列表
     */
    private List<ServerInfo> serverInfos;

    /**
     * 最终选择的服务提供者地址
     */
    private ServerInfo serverInfo;

    /**
     * 调用参数
     */
    private InvokerParam invokeParam;

    /**
     * 调用结果
     */
    private Object result;


}
