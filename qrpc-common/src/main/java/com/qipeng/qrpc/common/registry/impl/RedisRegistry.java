package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.registry.AbstractRegistry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class RedisRegistry extends AbstractRegistry {

    private static final Map<RegistryConfig, RedisRegistry> registryMap = new HashMap<>();
    private static final ScheduledThreadPoolExecutor subscribeExecutor;
    static {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("RedisRegistryThread-{}").build();
        subscribeExecutor = new ScheduledThreadPoolExecutor(10, threadFactory);
    }
    private final JedisPool jedisPool;
    private RedisRegistry(RegistryConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
        poolConfig.setMaxIdle(32);
        poolConfig.setMaxWaitMillis(100 * 1000);
        poolConfig.setTestOnBorrow(true);
        String address = config.getAddress();
        jedisPool = new JedisPool(poolConfig, URI.create(address));
    }

    public static RedisRegistry getInstance(RegistryConfig config) {
        RedisRegistry registry = registryMap.get(config);
        if (registry != null) {
            return registry;
        }
        synchronized (RedisRegistry.class) {
            registry = registryMap.get(config);
            if (registry != null) {
                return registry;
            }
            registry = new RedisRegistry(config);
            registryMap.put(config, registry);
            return registry;
        }
    }

    @Override
    public List<ServerInfo> doGetServerParam(String serviceName) {
        Set<String> servers;
        try (Jedis jedis = jedisPool.getResource()) {
            servers = jedis.smembers("/qrpc/" + serviceName);
        }
        if (servers == null || servers.isEmpty()) {
            throw new RpcException();
        }
        return servers.stream()
                      .map(s -> s.split(":"))
                      .map(s -> new ServerInfo(s[0], Integer.parseInt(s[1])))
                      .collect(Collectors.toList());
    }

    @Override
    public void subscribe(String serviceName) {
        subscribeExecutor.scheduleAtFixedRate(() -> refreshServerInfo(serviceName), 60, 60, TimeUnit.SECONDS);
    }

    private void refreshServerInfo(String serviceName) {
        try {
            List<ServerInfo> serverInfos = doGetServerParam(serviceName);
            getServiceMap().put(serviceName, serverInfos);
        } catch (Exception e) {
            log.error("监听注册中心服务出现异常", e);
        }
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
