package com.qipeng.qrpc.common.serialize;

public interface Serializer {
    /**
     * 序列化方法
     */
    Byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
