package com.qipeng.qrpc.common.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qipeng.qrpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonSerializer implements Serializer {

    private volatile static JsonSerializer instance;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static JsonSerializer getInstance() {

        if (instance == null) {
            synchronized (JsonSerializer.class) {
                if (instance == null) {
                    instance = new JsonSerializer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public Byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return json.getBytes();
        } catch (Exception e) {
            throw new RpcException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        String json = new String(bytes);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RpcException("反序列化失败");
        }
    }
}
