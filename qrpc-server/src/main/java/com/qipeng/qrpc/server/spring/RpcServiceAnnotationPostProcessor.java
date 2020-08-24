package com.qipeng.qrpc.server.spring;

import com.qipeng.qrpc.server.NettyRpcServer;
import com.qipeng.qrpc.server.ServiceProvider;
import com.qipeng.qrpc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RpcServiceAnnotationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
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
            NettyRpcServer.PROVIDER_MAP.put(serviceInterface.getName(), provider);
        }
        return bean;
    }
}
