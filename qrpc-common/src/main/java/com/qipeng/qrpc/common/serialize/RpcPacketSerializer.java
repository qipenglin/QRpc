package com.qipeng.qrpc.common.serialize;

import com.qipeng.qrpc.common.exception.PacketFormatException;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcPacket;
import com.qipeng.qrpc.common.util.ByteUtils;

public class RpcPacketSerializer {

    private static final byte MAGIC_NUM = 127;

    public static byte[] serialize(RpcPacket packet) {
        Serializer serializer = SerializerFactory.getSerializer();
        byte[] content = serializer.serialize(packet);
        byte[] bytes = new byte[content.length + 7];
        bytes[0] = MAGIC_NUM;
        bytes[1] = serializer.getSerializerAlgorithm();
        bytes[2] = packet.getPacketType();
        byte[] len = ByteUtils.intToBytes(content.length);
        System.arraycopy(len, 0, bytes, 3, 4);
        System.arraycopy(content, 0, bytes, 7, content.length);
        return bytes;
    }

    public static <T extends RpcPacket> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes[0] != MAGIC_NUM) {
            throw new RpcException("magic num is incorrect");
        }
        Byte packetType = bytes[2];
        if (!RpcPacket.PacketType.RESPONSE.equals(packetType) && !RpcPacket.PacketType.REQUEST.equals(packetType)) {
            throw new PacketFormatException("PacketType is incorrect");
        }
        Serializer serializer = SerializerFactory.getSerializer(bytes[1]);
        byte[] data = new byte[bytes.length - 7];
        System.arraycopy(bytes, 7, data, 0, data.length);
        return serializer.deserialize(data, clazz);
    }
}
