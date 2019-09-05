package com.qipeng.qrpc.common.serializer;

import com.qipeng.qrpc.common.RpcConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {

    private static Map<Byte, Class<? extends Serializer>> serializerTypeMap = new HashMap<>();

    private static Map<Byte, Serializer> serializerMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_SERIALIZER = "hessian";

    static {
        serializerTypeMap.put(SerializerAlgorithm.HESSIAN.getCode(), HessianSerializer.class);
    }


    public static Serializer getSerializer() {
        String serializerName = getSerializerName();
        return getSerializer(serializerName);
    }

    private static String getSerializerName() {
        String serializerName = RpcConfig.PROTOCOL_NAME;
        if (StringUtils.isBlank(serializerName)) {
            return DEFAULT_SERIALIZER;
        }
        return serializerName;
    }

    private static Serializer getSerializer(String serializerName) {
        if (StringUtils.isBlank(serializerName)) {
            return HessianSerializer.getInstance();
        }
        Byte code = SerializerAlgorithm.fromName(serializerName);
        return getSerializer(code);
    }

    public static Serializer getSerializer(Byte code) {
        if (code == null) {
            throw new RuntimeException("序列化器配置错误");
        }
        Serializer serializer = serializerMap.get(code);
        if (serializer != null) {
            return serializer;
        }
        Class<? extends Serializer> clazz = serializerTypeMap.get(code);
        if (clazz.equals(HessianSerializer.class)) {
            serializer = HessianSerializer.getInstance();
        } else {
            throw new RuntimeException("序列化器配置错误");
        }
        serializerMap.put(serializer.getSerializerAlgorithm(), serializer);
        return serializer;
    }
}
