package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.ServerParam;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    private static final String ROOT = "/qrpc";

    private static final String PROVIDERS = "providers";

    private final ZookeeperClient zkClient;

    private final static Map<RegistryConfig, ZookeeperRegistry> registryMap = new HashMap<>();

    private final Map<String, List<ServerParam>> serviceMap = new ConcurrentHashMap<>();

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

    private ZookeeperRegistry(RegistryConfig config) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String address = config.getHost() + ":" + config.getPort();
        zkClient = new ZookeeperClient(CuratorFrameworkFactory.newClient(address, retryPolicy));
    }

    @Override
    public List<ServerParam> doGetServerParam(String serviceName) {
        List<ServerParam> serverParams = serviceMap.get(serviceName);
        if (serverParams != null) {
            return serverParams;
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
    public boolean registerService(String serviceName, ServerParam serverParam) {
        String providerPath = buildProviderPath(serviceName);
        String serverAddr = serverParam.getHost() + ":" + serverParam.getPort();
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

    private List<ServerParam> buildServerParams(List<String> serverAddrList) {
        List<ServerParam> serverParams;
        if (CollectionUtils.isEmpty(serverAddrList)) {
            return Collections.emptyList();
        }
        serverParams = new ArrayList<>(serverAddrList.size());
        for (String provider : serverAddrList) {
            String[] serverAddr = provider.split(":");
            ServerParam serverParam = new ServerParam(serverAddr[0], Integer.parseInt(serverAddr[1]));
            serverParams.add(serverParam);
        }
        return serverParams;
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
            List<ServerParam> serverParams = buildServerParams(serverAddrList);
            serviceMap.put(serviceName, serverParams);
        }
    }
}
