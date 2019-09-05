package com.qipeng.qprc.server.spring;

import com.qipeng.qprc.server.RpcServer;
import com.qipeng.qprc.server.RpcServerFactory;
import com.qipeng.qprc.server.ServiceProvider;
import com.qipeng.qprc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcServiceAnnotationPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = beanClass.getAnnotation(RpcService.class);
            Class<?> serviceInterface = rpcService.serviceInterface();
            if (!serviceInterface.isInterface()) {
                throw new RuntimeException("serviceInterface of the RpcService on " + beanClass.getName() + " must be a interface");
            }
            if (!serviceInterface.isAssignableFrom(beanClass)) {
                throw new RuntimeException(beanClass.getName() + " must implements the interface " + serviceInterface.getName());
            }
            ServiceProvider provider = new ServiceProvider();
            provider.setServiceName(serviceInterface.getName());
            provider.setInstance(bean);
            RpcServer.PROVIDER_MAP.put(serviceInterface.getName(), provider);
        }
        return null;
    }
}
