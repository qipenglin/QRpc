package com.qipeng.qrpc.common.config;

import com.qipeng.qrpc.common.exception.RpcException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

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
    public static String NETWORK_MODEL = "netty";

    String registryPattern = "[zookeeper|redis]://(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";

    @Value("${qrpc.registry}")
    public void setRegistryUri(String registryUri) {
        Pattern pattern = Pattern.compile(registryUri);
        if (!pattern.matcher(registryUri).matches()) {
            throw new RpcException("REGISTRY_URI 格式有误");
        }
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
