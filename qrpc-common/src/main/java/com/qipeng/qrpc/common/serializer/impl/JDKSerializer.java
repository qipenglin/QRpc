package com.qipeng.qrpc.common.serializer.impl;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serializer.Serializer;
import com.qipeng.qrpc.common.serializer.SerializerProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JDKSerializer implements Serializer {

    private volatile static JDKSerializer instance;

    private JDKSerializer() {
    }

    public static JDKSerializer getInstance() {
        if (instance == null) {
            synchronized (JDKSerializer.class) {
                if (instance == null) {
                    instance = new JDKSerializer();
                    return instance;
                }
            }
        }
        return instance;
    }

    @Override
    public Byte getSerializerAlgorithm() {
        return SerializerProtocol.JDK.getCode();
    }

    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RpcException(ex);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (Exception ex) {
            throw new RpcException(ex);
        }
    }
}
