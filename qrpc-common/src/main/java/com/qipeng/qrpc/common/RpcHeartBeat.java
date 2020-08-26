package com.qipeng.qrpc.common;

/**
 * @author qipenglin
 * @date 2019-09-10 11:48
 **/
public class RpcHeartBeat extends RpcPacket {


    @Override
    public Byte getPacketType() {
        return PacketType.HEART_BEAT;
    }
}
