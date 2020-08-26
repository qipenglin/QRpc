package com.qipeng.qrpc.common.serialize;

import com.qipeng.qrpc.common.model.RpcPacket;

public class RpcPacketSerializer {

    private static final byte MAGIC_NUM = 127;

    public static byte[] encode(RpcPacket packet) {
        Serializer serializer = SerializerFactory.getSerializer();
        byte[] content = serializer.serialize(packet);
        byte[] bytes = new byte[content.length + 3];
        bytes[0] = MAGIC_NUM;
        bytes[1] = serializer.getSerializerAlgorithm();
        bytes[2] = packet.getPacketType();
        byte[] len = intToBytes(content.length);
        System.arraycopy(len, 3, bytes, 0, content.length);
        System.arraycopy(content, 7, bytes, 0, content.length);
        return bytes;
    }

    private static byte[] intToBytes(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

}
