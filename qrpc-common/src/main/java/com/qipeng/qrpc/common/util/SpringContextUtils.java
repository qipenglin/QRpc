package com.qipeng.qrpc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> typeClass) {
        return applicationContext.getBean(typeClass);
    }

    public static <T> T getBean(String beanName, Class<T> typeClass) {
        return applicationContext.getBean(beanName, typeClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
