package com.qipeng.qrpc.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qipenglin
 * @date 2019-09-10 11:48
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RpcHeartBeat extends RpcPacket {

    @Override
    public Byte getPacketType() {
        return PacketType.HEART_BEAT;
    }
}
