package com.qipeng.qrpc.server.spring;

import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryFactory;
import com.qipeng.qrpc.common.util.NetUtil;
import com.qipeng.qrpc.server.RpcServer;
import com.qipeng.qrpc.server.RpcServerFactory;
import com.qipeng.qrpc.server.ServiceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RpcServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${qrpc.protocol.port}")
    private int port;

    private volatile boolean isStarted;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        //防止重读
        if (applicationEvent.getApplicationContext().getParent() != null) {
            return;
        }
        startRpcServer();
    }

    private synchronized void startRpcServer() {
        if (isStarted) {
            return;
        }
        String localAddr = NetUtil.getLocalAddress();
        ServerInfo serverInfo = new ServerInfo(localAddr, port);
        RpcServerFactory.getServer().start(serverInfo);
        registerService(RpcServer.PROVIDER_MAP.values(), serverInfo);
        isStarted = true;

    }

    private void registerService(Collection<ServiceProvider> providers, ServerInfo serverInfo) {
        Registry registry = RegistryFactory.getDefaultRegistry();
        for (ServiceProvider provider : providers) {
            registry.registerService(provider.getServiceName(), serverInfo);
        }
    }
}
