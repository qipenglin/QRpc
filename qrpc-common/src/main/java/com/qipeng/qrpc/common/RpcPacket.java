package com.qipeng.qrpc.common;

import lombok.Data;

@Data
public abstract class RpcPacket {

    private Integer requestId;

    private Byte serializerType;

    public abstract Byte getPacketType();

    public interface PacketType {
        Byte HEART_BEAT = 0;
        Byte REQUEST = 1;
        Byte RESPONSE = 2;
    }
}
