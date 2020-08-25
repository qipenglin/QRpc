package com.qipeng.qrpc.server;

import com.qipeng.qrpc.common.ServerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RpcServer {

    Map<String, ServiceProvider> PROVIDER_MAP = new ConcurrentHashMap<>();

    void start(ServerInfo serverInfo);

}
