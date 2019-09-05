package com.qipeng.qrpc.common;

import com.qipeng.qrpc.common.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, RpcPacket> {
    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    private PacketCodecHandler() {
        super();
    }

    private static Map<Byte, Class<? extends RpcPacket>> packetTypeMap = new HashMap<>();

    private static final int MAGIC_NUM = 0x12345678;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int magicNum = byteBuf.readInt();
        if (magicNum != MAGIC_NUM) {
            throw new RuntimeException("包格式不符合规定");
        }
        Byte serializerType = byteBuf.readByte();
        Byte packetType = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Class<? extends RpcPacket> clazz = packetTypeMap.get(packetType);
        RpcPacket rpcPacket = SerializerFactory.getSerializer(serializerType).deserialize(clazz, bytes);
        out.add(rpcPacket);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcPacket packet, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        byte[] bytes = SerializerFactory.getSerializer().serialize(packet);
        byteBuf.writeInt(MAGIC_NUM);
        byteBuf.writeByte(packet.getSerializerType());
        byteBuf.writeByte(packet.getPacketType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }
}
