package com.qipeng.qrpc.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@EqualsAndHashCode(callSuper = true)
public class RpcRequest extends RpcPacket {

    private final static AtomicInteger requestIdSeed = new AtomicInteger(0);

    private Integer requestId;
    /**
     * 接口类
     */
    private Class<?> clazz;
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

    public RpcRequest() {
        this.requestId = requestIdSeed.addAndGet(1);
    }

    @Override
    public Byte getPacketType() {
        return PacketType.REQUEST;
    }
}
