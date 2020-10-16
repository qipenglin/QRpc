package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.AbstractRegistry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class RedisRegistry extends AbstractRegistry {

    private static final Map<RegistryConfig, RedisRegistry> registryMap = new HashMap<>();

    private static final ScheduledThreadPoolExecutor subscribeExecutor;

    static {
        subscribeExecutor = new ScheduledThreadPoolExecutor(1);
        subscribeExecutor.scheduleAtFixedRate(RedisRegistry::refreshServerInfo, 60, 60, TimeUnit.SECONDS);
    }

    private final JedisPool jedisPool;
    @Getter
    private final Set<String> subscribedServices;

    private RedisRegistry(RegistryConfig config) {
        String address = config.getAddress();
        jedisPool = new JedisPool(URI.create(address));
        subscribedServices = new HashSet<>();
    }

    public static RedisRegistry getInstance(RegistryConfig config) {
        RedisRegistry registry = registryMap.get(config);
        if (registry != null) {
            return registry;
        }
        synchronized (RedisRegistry.class) {
            registry = registryMap.computeIfAbsent(config, RedisRegistry::new);
            registryMap.put(config, registry);
            return registry;
        }
    }

    private static void refreshServerInfo() {
        try {
            if (MapUtils.isEmpty(registryMap)) {
                return;
            }
            for (RedisRegistry registry : registryMap.values()) {
                if (!CollectionUtils.isEmpty(registry.getSubscribedServices())) {
                    continue;
                }
                for (String serviceName : registry.getSubscribedServices()) {
                    List<ServerInfo> serverInfos = registry.doGetServerParam(serviceName);
                    registry.getServiceMap().put(serviceName, serverInfos);
                }
            }
        } catch (Exception e) {
            log.error("监听注册中心服务出现异常", e);
        }
    }

    @Override
    public List<ServerInfo> doGetServerParam(String serviceName) {
        Set<String> servers;
        try (Jedis jedis = jedisPool.getResource()) {
            servers = jedis.smembers("/qrpc/" + serviceName);
        }
        if (servers == null || servers.isEmpty()) {
            return new ArrayList<>();
        }
        return servers.stream()
                      .map(s -> s.split(":"))
                      .map(s -> new ServerInfo(s[0], Integer.parseInt(s[1])))
                      .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String serviceName) {
        subscribedServices.add(serviceName);
    }

    @Override
    public boolean registerService(String serviceName, ServerInfo serverInfo) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("/qrpc/" + serviceName, serverInfo.getHost() + ":" + serverInfo.getPort());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
