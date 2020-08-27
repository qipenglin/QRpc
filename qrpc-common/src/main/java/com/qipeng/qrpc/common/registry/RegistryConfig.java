package com.qipeng.qrpc.common.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistryConfig {

    private RegistryProtocol protocol;
    private String address;
}
