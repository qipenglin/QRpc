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

    public static String REGISTRY;
    public static String PROTOCOL;
    public static String NETWORK_MODEL = "netty";

    String registryPattern = "[zookeeper|redis]://(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";

    @Value("${qrpc.registry}")
    public void setRegistry(String registry) {
        Pattern pattern = Pattern.compile(registry);
        if (!pattern.matcher(registry).matches()) {
            throw new RpcException("REGISTRY_URI 格式有误");
        }
        REGISTRY = registry;
    }

    @Value("${qrpc.protocol}")
    public void setProtocolName(String protocol) {
        PROTOCOL = protocol;
    }
}
