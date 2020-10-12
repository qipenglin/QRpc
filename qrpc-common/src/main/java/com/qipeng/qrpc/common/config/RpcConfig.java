package com.qipeng.qrpc.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author qipenglin
 */
@Component
public class RpcConfig extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered, EnvironmentAware {

    public static String REGISTRY;
    public static String PROTOCOL;
    public static String NETWORK_MODEL;
    public static String PROXY;


    @Value("${qrpc.registry}")
    public void setRegistry(String registry) {
        REGISTRY = registry;
    }

    @Value("${qrpc.protocol:hessian}")
    public void setProtocolName(String protocol) {
        PROTOCOL = protocol;
    }

    @Value("${qrpc.networkModel:netty}")
    public void setNetworkModel(String networkModel) {
        NETWORK_MODEL = networkModel;
    }

    @Value("${qrpc.proxy:cglib}")
    public void setProxy(String proxy) {
        PROXY = proxy;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        PROXY = environment.getProperty("qrpc.proxy");
    }
}
