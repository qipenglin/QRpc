package com.qipeng.qprc.server.spring;

import com.qipeng.qprc.server.RpcServer;
import com.qipeng.qprc.server.RpcServerFactory;
import com.qipeng.qprc.server.ServiceProvider;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.registry.Registry;
import com.qipeng.qrpc.common.registry.RegistryFactory;
import com.qipeng.qrpc.common.util.NetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServerStarter implements ApplicationListener {

    @Value("${qrpc.protocol.port}")
    private int port;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        String localAddr = NetUtil.getLocalAddress();
        ServerParam serverParam = new ServerParam(localAddr, port);
        RpcServerFactory.getServer().start(serverParam);
        registerService(RpcServer.PROVIDER_MAP.values(), serverParam);
    }

    private void registerService(Collection<ServiceProvider> providers, ServerParam serverParam) {
        Registry registry = RegistryFactory.getRegistry();
        for (ServiceProvider provider : providers) {
            registry.registerService(provider.getServiceName(), serverParam);
        }
    }
}
