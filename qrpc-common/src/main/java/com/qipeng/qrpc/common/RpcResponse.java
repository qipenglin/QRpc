package com.qipeng.qrpc.common;

import lombok.Data;

@Data
public class RpcResponse extends RpcPacket {

    private Object result;

    private String resultType;

    @Override
    public Byte getPacketType() {
        return PacketType.RESPONSE;
    }
}
