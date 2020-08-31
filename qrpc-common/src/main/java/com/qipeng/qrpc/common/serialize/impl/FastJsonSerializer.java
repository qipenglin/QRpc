package com.qipeng.qrpc.common.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerProtocol;

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
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }
}
