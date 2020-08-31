package com.qipeng.qrpc.common.util;

import com.qipeng.qrpc.common.exception.PacketFormatException;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.RpcPacket;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerFactory;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/8/31 7:27 下午
 */
public class ByteUtils {

    private static final byte MAGIC_NUM = 127;

    public static <T extends RpcPacket> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes[0] != MAGIC_NUM) {
            throw new RpcException("");
        }
        Byte packetType = bytes[1];
        if (!RpcPacket.PacketType.RESPONSE.equals(packetType) &&
            !RpcPacket.PacketType.REQUEST.equals(packetType)) {
            throw new PacketFormatException("PacketType is incorrect");
        }
        Serializer serializer = SerializerFactory.getSerializer(bytes[2]);
        byte[] data = new byte[bytes.length - 3];
        System.arraycopy(bytes, 3, data, 0, data.length);
        return serializer.deserialize(data, clazz);
    }

    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }
}
