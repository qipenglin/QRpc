package com.qipeng.qrpc.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author qipenglin
 */
@Component
public class RpcConfig {

    public static String REGISTRY;
    public static String PROTOCOL;
    public static String NETWORK_MODEL;


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
}
