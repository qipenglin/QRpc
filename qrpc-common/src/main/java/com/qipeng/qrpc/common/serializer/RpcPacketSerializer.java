package com.qipeng.qrpc.common.serializer;

import com.qipeng.qrpc.common.RpcPacket;

public class RpcPacketSerializer {

    private static final byte[] MAGIC_NUM = {0x12, 0x34, 0x56, 0x78};

    public static byte[] encode(RpcPacket packet) {
        Serializer serializer = SerializerFactory.getSerializer();
        byte[] content = serializer.serialize(packet);
        byte[] bytes = new byte[content.length + 6];
        System.arraycopy(MAGIC_NUM, 0, bytes, 0, 4);
        bytes[4] = serializer.getSerializerAlgorithm();
        bytes[5] = packet.getPacketType();
        byte[] len = intToBytes(content.length);
        System.arraycopy(len, 6, bytes, 0, content.length);
        System.arraycopy(content, 10, bytes, 0, content.length);
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
