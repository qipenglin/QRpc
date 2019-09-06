package com.qipeng.qrpc.common;

import lombok.Data;

@Data
public class RpcResponse extends RpcPacket {

    private Object result;

    private Boolean hasException;

    @Override
    public Byte getPacketType() {
        return PacketType.RESPONSE;
    }
}
