package com.qipeng.qrpc.common.registry;

import com.qipeng.qrpc.common.RpcConfig;
import com.qipeng.qrpc.common.ServerParam;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZookeeperRegistry implements Registry {

    private static final String ROOT = "/qrpc";

    private static final String PROVIDERS = "providers";

    private static final String CONSUMERS = "consumers";

    private ZookeeperClient zkClient;

    private Map<String, List<ServerParam>> serviceMap = new ConcurrentHashMap<>();

    private Set<String> servicesListened = new HashSet<>();

    private volatile static ZookeeperRegistry instance;

    public static ZookeeperRegistry getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ZookeeperRegistry.class) {
            if (instance == null) {
                instance = new ZookeeperRegistry();
            }
            return instance;
        }
    }

    private ZookeeperRegistry() {
        String zkAddress = RpcConfig.REGISTRY_ADDRESS;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = new ZookeeperClient(CuratorFrameworkFactory.newClient(zkAddress, retryPolicy));
    }

    private String getZookeeperRegistry() {
        return "";
    }

    @Override
    public List<ServerParam> getServerParam(String serviceName) {
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
            zkClient.registerPathChildListener(providerPath, new ServiceListener(serviceName));
            return buildServerParams(serverAddrList);
        } catch (Exception e) {
            log.error("从注册中心获取");
            return Collections.emptyList();
        }
    }

    @Override
    public boolean registerService(String serviceName, ServerParam serverParam) {
        String providerPath = buildProviderPath(serviceName);
        String serverAddr = serverParam.getHost() + ":" + serverParam.getPort();
        if (!zkClient.checkExists(providerPath)) {
            zkClient.createPerNode(providerPath);
        }
        zkClient.createEphNode(providerPath + "/" + serverAddr);
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
