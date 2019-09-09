package com.qipeng.qrpc.server;

import lombok.Data;

@Data
public class ServiceProvider {
    private String serviceName;

    private Object instance;
}
