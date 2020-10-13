package com.qipeng.qrpc.client.spring;

import com.alibaba.spring.beans.factory.annotation.AbstractAnnotationBeanPostProcessor;
import com.qipeng.qrpc.client.annotation.RpcReference;
import com.qipeng.qrpc.client.proxy.ProxyFactory;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.reflect.Field;

/**
 *
 * Company: www.vivo.com
 * Copyright: (c) All Rights Reserved.
 * Information:
 *
 * @author qipenglin
 * @date Created at 2020/10/12 7:48 下午
 */
//@Component
public class RpcReferenceAnnotationProcessor extends AbstractAnnotationBeanPostProcessor {

    public RpcReferenceAnnotationProcessor() {
        super(RpcReference.class);
    }

    @Override
    protected Object doGetInjectedBean(AnnotationAttributes annotationAttributes, Object o, String s, Class<?> aClass, InjectionMetadata.InjectedElement injectedElement) throws Exception {
        Field field = (Field) injectedElement.getMember();
        return ProxyFactory.getProxy(field.getType());
    }

    @Override
    protected String buildInjectedObjectCacheKey(AnnotationAttributes annotationAttributes, Object o, String s, Class<?> aClass, InjectionMetadata.InjectedElement injectedElement) {
        Field field = (Field) injectedElement.getMember();
        Class<?> clazz = field.getType();
        return clazz.getName();
    }
}
