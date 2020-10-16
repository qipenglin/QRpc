package com.qipeng.qrpc.common.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistryConfig {

    private final RegistryProtocol protocol;
    private final String address;

    public String toString() {
        return protocol + "://" + address;
    }

}
