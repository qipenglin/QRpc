package com.qipeng.qrpc.common.serializer;

public class HessianSerializer implements Serializer {

    private volatile static HessianSerializer instance;

    private HessianSerializer() {
    }

    public static HessianSerializer getInstance() {
        if (instance == null) {
            synchronized (HessianSerializer.class) {
                if (instance == null) {
                    instance = new HessianSerializer();
                    return instance;
                }
            }
        }
        return instance;
    }

    public Byte getSerializerAlgorithm() {
        return SerializerAlgorithm.HESSIAN.getCode();
    }

    public byte[] serialize(Object object) {
        return new byte[0];
    }

    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return null;
    }
}
