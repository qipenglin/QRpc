package com.qipeng.qrpc.common.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerProtocol;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
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

    @Override
    public Byte getSerializerAlgorithm() {
        return SerializerProtocol.HESSIAN.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(outputStream);
        try {
            hessianOutput.writeObject(object);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput(inputStream);
        try {
            return (T) hessianInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
