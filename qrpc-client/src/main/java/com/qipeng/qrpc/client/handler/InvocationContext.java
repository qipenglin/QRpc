package com.qipeng.qrpc.client.handler;

import com.qipeng.qrpc.common.InvokerParam;
import com.qipeng.qrpc.common.ServerParam;
import lombok.Data;

@Data
public class InvocationContext {

    /**
     * 调用参数
     */
    private InvokerParam invokerParam;

    /**
     * 最终选择的服务提供者地址
     */
    private ServerParam serverParam;

    /**
     * 调用方法的参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

}
