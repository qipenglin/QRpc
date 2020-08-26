package com.qipeng.qrpc.common.serialize;

import com.qipeng.qrpc.common.RpcPacket;
import com.qipeng.qrpc.common.exception.PacketFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketReader {

    private static final byte MAGIC_NUM = 127;

    public static <T extends RpcPacket> T readRpcPacket(Socket socket, Class<T> clazz) throws IOException {
        InputStream inputStream = socket.getInputStream();
        checkMagicNum(inputStream);
        Serializer serializer = parseSerializer(inputStream);
        checkPacketType(inputStream, clazz);
        int len = parseLength(inputStream);
        byte[] bytes = getBody(inputStream, len);
        RpcPacket packet = null;
        try {
            packet = serializer.deserialize(clazz, bytes);
        } catch (Exception e) {
            throw new PacketFormatException("Packet deserialize error", e);
        }
        return (T) packet;
    }

    private static int parseLength(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[4];
        inputStream.read(bytes, 0, 4);
        int len = byte4ToInt(bytes, 0);
        return len;
    }


    private static void checkMagicNum(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        if (bytes[0] != MAGIC_NUM) {
            throw new PacketFormatException("Magic No is incorrect");
        }
    }

    private static <T extends RpcPacket> void checkPacketType(InputStream inputStream, Class<T> clazz) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        Byte packetType = bytes[0];
        if (!RpcPacket.PacketType.RESPONSE.equals(packetType) &&
            !RpcPacket.PacketType.REQUEST.equals(packetType)) {
            throw new PacketFormatException("PacketType is incorrect");
        }
    }

    private static Serializer parseSerializer(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1];
        inputStream.read(bytes, 0, 1);
        return SerializerFactory.getSerializer(bytes[0]);
    }

    private static byte[] getBody(InputStream inputStream, int len) throws IOException {
        byte[] bytes;
        bytes = new byte[len];
        inputStream.read(bytes, 0, len);
        return bytes;
    }

    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

}
