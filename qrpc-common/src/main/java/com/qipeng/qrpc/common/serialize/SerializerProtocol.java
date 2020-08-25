package com.qipeng.qrpc.common.serialize;

import com.qipeng.qrpc.common.serialize.impl.FastJsonSerializer;
import com.qipeng.qrpc.common.serialize.impl.HessianSerializer;
import com.qipeng.qrpc.common.serialize.impl.JDKSerializer;
import com.qipeng.qrpc.common.serialize.impl.ProtoBufSerializer;

public enum SerializerProtocol {

    FAST_JSON("fastjson", (byte) 1, FastJsonSerializer.class),
    HESSIAN("hessian", (byte) 2, HessianSerializer.class),
    PROTO_BUF("protobuf", (byte) 3, ProtoBufSerializer.class),
    JDK("jdk", (byte) 4, JDKSerializer.class);

    private String name;

    private Byte code;

    private Class<? extends Serializer> serializerClass;

    SerializerProtocol(String name, Byte code, Class<? extends Serializer> serializerClass) {
        this.name = name;
        this.code = code;
        this.serializerClass = serializerClass;
    }

    public String getName() {
        return name;
    }

    public Byte getCode() {
        return code;
    }

    public static SerializerProtocol getByName(String name) {
        for (SerializerProtocol serializerProtocol : values()) {
            if (name.equals(serializerProtocol.name)) {
                return serializerProtocol;
            }
        }
        return null;
    }

    public static SerializerProtocol getByCode(Byte code) {
        for (SerializerProtocol serializerProtocol : values()) {
            if (serializerProtocol.code.equals(code)) {
                return serializerProtocol;
            }
        }
        return null;
    }
}

