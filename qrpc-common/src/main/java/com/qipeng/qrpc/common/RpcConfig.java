package com.qipeng.qrpc.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author qipenglin
 */
@Component
public class RpcConfig {

    public static String REGISTRY_URI;

    public static String REGISTRY_PROTOCOL;

    public static String REGISTRY_ADDRESS;

    public static String PROTOCOL_NAME;

    public static String PROTOCOL_PORT;

    @Value("${qrpc.registry}")
    public void setRegistryUri(String registryUri) {
        REGISTRY_URI = registryUri;
    }

    @Value("${qrpc.registry.protocol}")
    public void setRegistryProtocol(String registryProtocol) {
        REGISTRY_PROTOCOL = registryProtocol;
    }

    @Value("${qrpc.registry.address}")
    public void setRegistryAddress(String registryAddress) {
        REGISTRY_ADDRESS = registryAddress;
    }

    @Value("${qrpc.protocol.name}")
    public void setProtocolName(String protocolName) {
        PROTOCOL_NAME = protocolName;
    }

    @Value("${qrpc.protocol.port}")
    public void setProtocolPort(String protocolPort) {
        PROTOCOL_PORT = protocolPort;
    }


}
