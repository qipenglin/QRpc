package com.qipeng.qrpc.client.spring;

import com.qipeng.qrpc.client.annotation.RpcReference;
import com.qipeng.qrpc.client.proxy.RpcProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class RpcReferenceAnnotationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RpcReference.class)) {
                try {
                    Object proxy = RpcProxyFactory.createProxy(field.getType());
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    log.error("处理RpcReference注解失败,类名:{},字段:{}", clazz.getName(), field.getName());
                }
            }
        }
        return bean;
    }
}
