package com.qipeng.qrpc.common.serializer;

public enum SerializerAlgorithm {

    JSON("json", (byte) 1),
    HESSIAN("hessian", (byte) 2),
    PROTO_BUF("proto_buf", (byte) 3);

    private String name;

    private Byte code;

    SerializerAlgorithm(String name, Byte code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public static Byte fromName(String name) {
        for (SerializerAlgorithm serializerAlgorithm : values()) {
            if (name.equals(serializerAlgorithm.name)) {
                return serializerAlgorithm.code;
            }
        }
        return null;
    }
}

