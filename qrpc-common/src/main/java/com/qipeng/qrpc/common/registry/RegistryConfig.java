package com.qipeng.qrpc.common.registry;

import lombok.Data;

@Data
public class RegistryConfig {
    private RegistryProtocol protocol;
    private String host;
    private int port;
}
