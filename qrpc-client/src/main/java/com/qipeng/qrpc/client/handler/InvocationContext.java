package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.ServerInfo;
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
     * 超时时间
     */
    private int timeout;

    /**
     * rpc请求
     */
    private RpcRequest rpcRequest;

    /**
     * 调用结果
     */
    private Object result;


}
