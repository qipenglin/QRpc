package com.qipeng.qrpc.common.serialize.impl;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.serialize.Serializer;
import com.qipeng.qrpc.common.serialize.SerializerProtocol;

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
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (Exception ex) {
            throw new RpcException(ex);
        }
    }
}
