package com.qipeng.qrpc.common;

import lombok.Data;

@Data
public class InvokerParam {

    /**
     * 接口类
     */
    private Class clazz;

    /**
     * 调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;
}
