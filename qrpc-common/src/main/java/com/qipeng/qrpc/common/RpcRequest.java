package com.qipeng.qrpc.common;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class RpcRequest extends RpcPacket {

    private Integer requestId;

    private static AtomicInteger requestIdSeed = new AtomicInteger(0);

    public RpcRequest() {
        super();
        setRequestId(requestIdSeed.addAndGet(1));
    }

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

    @Override
    public Byte getPacketType() {
        return PacketType.REQUEST;
    }
}
