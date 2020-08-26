package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.AbstractRegistry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import com.qipeng.qrpc.common.util.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private static final String ROOT = "/qrpc";

    private static final String PROVIDERS = "providers";
    private final static Map<RegistryConfig, ZookeeperRegistry> registryMap = new HashMap<>();
    private final ZookeeperClient zkClient;
    private final Map<String, List<ServerInfo>> serviceMap = new ConcurrentHashMap<>();

    private ZookeeperRegistry(RegistryConfig config) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String address = config.getHost() + ":" + config.getPort();
        zkClient = new ZookeeperClient(CuratorFrameworkFactory.newClient(address, retryPolicy));
    }

    public static ZookeeperRegistry getInstance(RegistryConfig config) {
        ZookeeperRegistry registry = registryMap.get(config);
        if (registry != null) {
            return registry;
        }
        synchronized (ZookeeperRegistry.class) {
            registry = registryMap.get(config);
            if (registry != null) {
                return registry;
            }
            registry = new ZookeeperRegistry(config);
            registryMap.put(config, registry);
            return registry;
        }
    }

    @Override
    public List<ServerInfo> doGetServerParam(String serviceName) {
        List<ServerInfo> serverInfos = serviceMap.get(serviceName);
        if (serverInfos != null) {
            return serverInfos;
        }
        try {
            String providerPath = buildProviderPath(serviceName);
            if (!zkClient.checkExists(providerPath)) {
                return Collections.emptyList();
            }
            List<String> serverAddrList = zkClient.getChildren(providerPath);

            return buildServerParams(serverAddrList);
        } catch (Exception e) {
            log.error("从注册中心获取");
            return Collections.emptyList();
        }
    }

    @Override
    protected void subscribe(String serviceName) {
        String providerPath = buildProviderPath(serviceName);
        zkClient.registerPathChildListener(providerPath, new ServiceListener(serviceName));
    }

    @Override
    public boolean registerService(String serviceName, ServerInfo serverInfo) {
        String providerPath = buildProviderPath(serviceName);
        String serverAddr = serverInfo.getHost() + ":" + serverInfo.getPort();
        if (!zkClient.checkExists(providerPath)) {
            zkClient.createPerNode(providerPath);
        }
        String addrPath = providerPath + "/" + serverAddr;
        if (!zkClient.checkExists(addrPath)) {
            zkClient.createEphNode(addrPath);
        }
        return true;
    }

    private String buildProviderPath(String serviceName) {
        return ROOT + "/" + serviceName + "/" + PROVIDERS;
    }

    private List<ServerInfo> buildServerParams(List<String> serverAddrList) {
        List<ServerInfo> serverInfos;
        if (CollectionUtils.isEmpty(serverAddrList)) {
            return Collections.emptyList();
        }
        serverInfos = new ArrayList<>(serverAddrList.size());
        for (String provider : serverAddrList) {
            String[] serverAddr = provider.split(":");
            ServerInfo serverInfo = new ServerInfo(serverAddr[0], Integer.parseInt(serverAddr[1]));
            serverInfos.add(serverInfo);
        }
        return serverInfos;
    }

    class ServiceListener implements PathChildrenCacheListener {
        private String serviceName;

        private ServiceListener(String serviceName) {
            this.serviceName = serviceName;
        }

        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            String providerPath = buildProviderPath(serviceName);
            List<String> serverAddrList = zkClient.getChildren(providerPath);
            List<ServerInfo> serverInfos = buildServerParams(serverAddrList);
            serviceMap.put(serviceName, serverInfos);
        }
    }
}
