package com.qipeng.qprc.server;

import lombok.Data;

@Data
public class ServiceProvider {
    private String serviceName;

    private Object instance;
}
