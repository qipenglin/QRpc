package com.qipeng.qrpc.common.serialize;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.serialize.impl.FastJsonSerializer;
import com.qipeng.qrpc.common.serialize.impl.HessianSerializer;
import com.qipeng.qrpc.common.serialize.impl.JDKSerializer;
import com.qipeng.qrpc.common.serialize.impl.ProtoBufSerializer;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {

    private static final Map<Byte, Serializer> serializerMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_SERIALIZER = "hessian";

    /**
     * 获取配置的序列化器（调用端使用）
     *
     * @return
     */
    public static Serializer getSerializer() {
        String serializerName = RpcConfig.PROTOCOL_NAME;
        if (StringUtils.isBlank(serializerName)) {
            serializerName = DEFAULT_SERIALIZER;
        }
        SerializerProtocol algorithm = SerializerProtocol.getByName(serializerName);
        return getSerializer(algorithm);
    }

    /**
     * 根据code获取序列化器（服务端使用该方法,用客户端使用的序列化器进行反序列化）
     *
     * @return
     */
    public static Serializer getSerializer(Byte serializerType) {
        SerializerProtocol algorithm = SerializerProtocol.getByCode(serializerType);
        return getSerializer(algorithm);
    }

    private static Serializer getSerializer(SerializerProtocol algorithm) {
        if (algorithm == null) {
            throw new RuntimeException("序列化器配置错误");
        }
        Serializer serializer = serializerMap.get(algorithm.getCode());
        if (serializer != null) {
            return serializer;
        }
        switch (algorithm) {
            case JDK:
                serializer = JDKSerializer.getInstance();
                break;
            case FAST_JSON:
                serializer = FastJsonSerializer.getInstance();
                break;
            case PROTO_BUF:
                serializer = ProtoBufSerializer.getInstance();
                break;
            case HESSIAN:
            default:
                serializer = HessianSerializer.getInstance();
        }
        serializerMap.put(algorithm.getCode(), serializer);
        return serializer;
    }
}
