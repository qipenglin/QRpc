package com.qipeng.qrpc.common.netty.codec;

import com.qipeng.qrpc.common.model.RpcHeartBeat;
import com.qipeng.qrpc.common.model.RpcPacket;
import com.qipeng.qrpc.common.model.RpcRequest;
import com.qipeng.qrpc.common.model.RpcResponse;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class PacketCodecHandler extends ByteToMessageCodec<RpcPacket> {
    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();
    private static final byte MAGIC_NUM = 127;
    private static Map<Byte, Class<? extends RpcPacket>> packetTypeMap = new HashMap<>();

    static {
        packetTypeMap.put(RpcPacket.PacketType.HEART_BEAT, RpcHeartBeat.class);
        packetTypeMap.put(RpcPacket.PacketType.REQUEST, RpcRequest.class);
        packetTypeMap.put(RpcPacket.PacketType.RESPONSE, RpcResponse.class);
    }

    private PacketCodecHandler() {
        super();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        byte magicNum = byteBuf.readByte();
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
    protected void encode(ChannelHandlerContext ctx, RpcPacket packet, ByteBuf byteBuf) {
        Serializer serializer = SerializerFactory.getSerializer();
        byte[] bytes = serializer.serialize(packet);
        byteBuf.writeByte(MAGIC_NUM);
        byteBuf.writeByte(serializer.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getPacketType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}