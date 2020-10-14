package com.qipeng.qrpc.server.spring;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.server.RpcServer;
import com.qipeng.qrpc.server.ServiceProvider;
import com.qipeng.qrpc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RpcServiceAnnotationPostProcessor implements BeanPostProcessor, EnvironmentAware {

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
                throw new RpcException("serviceInterface of the RpcService on " + beanClass.getName() + " must be a interface");
            }
            if (!serviceInterface.isAssignableFrom(beanClass)) {
                throw new RpcException(beanClass.getName() + " must implements the interface " + serviceInterface.getName());
            }
            ServiceProvider provider = new ServiceProvider();
            provider.setServiceName(serviceInterface.getName());
            provider.setInstance(bean);
            RpcServer.PROVIDER_MAP.put(serviceInterface.getName(), provider);
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        RpcConfig.setEnvironment(environment);
    }
}
