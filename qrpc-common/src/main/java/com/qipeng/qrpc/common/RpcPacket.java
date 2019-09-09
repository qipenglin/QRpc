package com.qipeng.qrpc.common;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class RpcPacket implements Serializable {

    private Byte serializerType;

    public abstract Byte getPacketType();

    public interface PacketType {
        Byte HEART_BEAT = 0;
        Byte REQUEST = 1;
        Byte RESPONSE = 2;
    }
}
