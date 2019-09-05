package com.qipeng.qrpc.common.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
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
        return SerializerAlgorithm.HESSIAN.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(outputStream);
        try {
            hessianOutput.writeObject(object);
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput(inputStream);
        T o = null;
        try {
            o = (T) hessianInput.readObject();
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        return o;
    }
}
