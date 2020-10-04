package com.qipeng.qrpc.common.netty.codec;

import com.qipeng.qrpc.common.model.RpcHeartBeat;
import com.qipeng.qrpc.common.model.RpcPacket;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketCodecHandler extends ByteToMessageCodec<RpcPacket> {
    private static final byte MAGIC_NUM = 127;
    private static final Map<Byte, Class<? extends RpcPacket>> PACKET_TYPE_MAP = new HashMap<>();

    static {
        PACKET_TYPE_MAP.put(RpcPacket.PacketType.HEART_BEAT, RpcHeartBeat.class);
        PACKET_TYPE_MAP.put(RpcPacket.PacketType.REQUEST, RpcRequest.class);
        PACKET_TYPE_MAP.put(RpcPacket.PacketType.RESPONSE, RpcResponse.class);
    }

    public PacketCodecHandler() {
        super();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte magicNum = byteBuf.readByte();
        if (magicNum != MAGIC_NUM) {
            throw new RuntimeException("包格式不符合规定");
        }
        Byte packetType = byteBuf.readByte();
        Byte serializerType = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Class<? extends RpcPacket> clazz = PACKET_TYPE_MAP.get(packetType);
        RpcPacket rpcPacket = SerializerFactory.getSerializer(serializerType).deserialize(bytes, clazz);
        out.add(rpcPacket);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcPacket packet, ByteBuf byteBuf) {
        Serializer serializer = SerializerFactory.getSerializer();
        byteBuf.writeByte(MAGIC_NUM);
        byteBuf.writeByte(packet.getPacketType());
        byteBuf.writeByte(serializer.getSerializerAlgorithm());
        byte[] bytes = serializer.serialize(packet);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
