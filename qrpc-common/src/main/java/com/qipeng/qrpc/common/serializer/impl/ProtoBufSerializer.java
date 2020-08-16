package com.qipeng.qrpc.common.serializer.impl;

import com.qipeng.qrpc.common.serializer.Serializer;
import com.qipeng.qrpc.common.serializer.SerializerProtocol;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoBufSerializer implements Serializer {

    private volatile static ProtoBufSerializer instance;

    private ProtoBufSerializer() {
    }

    public static ProtoBufSerializer getInstance() {
        if (instance == null) {
            synchronized (ProtoBufSerializer.class) {
                if (instance == null) {
                    instance = new ProtoBufSerializer();
                    return instance;
                }
            }
        }
        return instance;
    }

    /**
     * 避免每次序列化都重新申请Buffer空间
     */
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public Byte getSerializerAlgorithm() {
        return SerializerProtocol.PROTO_BUF.getCode();
    }

    /**
     * 缓存Schema
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        byte[] data;
        try {
            Class clazz = obj.getClass();
            Schema schema = getSchema(clazz);
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }


    @Override
    public <T> T deserialize(Class<T> clazz, byte[] data) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }
}
