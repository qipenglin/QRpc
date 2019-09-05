package com.qipeng.qrpc.client.spring;

import com.qipeng.qrpc.client.annotation.RpcReference;
import com.qipeng.qrpc.client.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class RpcReferenceAnnotationPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(RpcReference.class)) {
                try {
                    Object proxy = ProxyFactory.getProxy(field.getClass());
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    log.error("处理RpcReference注解失败,类名:{},字段:{}", clazz.getName(), field.getName());
                }
            }
        }
        return null;
    }
}
