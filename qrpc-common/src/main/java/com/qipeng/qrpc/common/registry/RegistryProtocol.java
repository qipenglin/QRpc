package com.qipeng.qrpc.common.registry;

public enum RegistryProtocol {

    ZOOKEEPER("zookeeper"),
    REDIS("redis");

    private String protocol;

    RegistryProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static RegistryProtocol forName(String protocol) {
        for (RegistryProtocol value : values()) {
            if (value.protocol.equals(protocol)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return protocol;
    }
}
