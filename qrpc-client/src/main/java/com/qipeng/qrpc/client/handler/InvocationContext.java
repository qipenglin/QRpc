package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.InvokerParam;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.registry.Registry;
import lombok.Data;

import java.util.List;

@Data
public class InvocationContext {

    /**
     * 注册中心
     */
    private Registry registry;
    /**
     * 可供选择的服务器列表
     */
    private List<ServerParam> serverParams;

    /**
     * 最终选择的服务提供者地址
     */
    private ServerParam serverParam;

    /**
     * 调用参数
     */
    private InvokerParam invokeParam;

    /**
     * 调用结果
     */
    private Object result;


}
