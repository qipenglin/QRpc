package com.qipeng.qrpc.common.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.qipeng.qrpc.common.serializer.Serializer;
import com.qipeng.qrpc.common.serializer.SerializerProtocol;

public class FastJsonSerializer implements Serializer {

    private volatile static FastJsonSerializer instance;

    private FastJsonSerializer() {
    }

    public static FastJsonSerializer getInstance() {
        if (instance == null) {
            synchronized (HessianSerializer.class) {
                if (instance == null) {
                    instance = new FastJsonSerializer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public Byte getSerializerAlgorithm() {
        return SerializerProtocol.FAST_JSON.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONString(object).getBytes();

    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
