package com.qipeng.qrpc.common.config;

import com.qipeng.qrpc.common.exception.RpcException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * @author qipenglin
 */
public class RpcConfig {

    private static Environment environment;

    public static void setEnvironment(Environment environment) {
        RpcConfig.environment = environment;
    }

    public static String getProxy() {
        return environment.getProperty("qrpc.proxy", "cglib");
    }

    public static String getNetworkModel() {
        return environment.getProperty("qrpc.networkModel", "netty");
    }

    public static String getProtocol() {
        return environment.getProperty("qrpc.protocol", "hessian");
    }

    public static int getServerPort() {
        return Integer.parseInt(environment.getProperty("qrpc.server.port", "20200"));
    }

    public static String getRegistry() {
        String registry = environment.getProperty("qrpc.registry");
        if (StringUtils.isBlank(registry)) {
            throw new RpcException("缺少配置项qrpc.registry，请检查后重新启动");
        }
        return registry;
    }
}
