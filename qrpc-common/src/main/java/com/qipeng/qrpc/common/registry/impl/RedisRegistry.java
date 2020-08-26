package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.config.RpcConfig;
import com.qipeng.qrpc.common.model.ServerInfo;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.registry.AbstractRegistry;
import com.qipeng.qrpc.common.registry.RegistryConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisRegistry extends AbstractRegistry {

    private static Map<RegistryConfig, RedisRegistry> registryMap = new HashMap<>();
    private JedisPool jedisPool;

    private RedisRegistry(RegistryConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
        poolConfig.setMaxIdle(32);
        poolConfig.setMaxWaitMillis(100 * 1000);
        poolConfig.setTestOnBorrow(true);
        String address = config.getHost() + ":" + config.getPort();
        jedisPool = new JedisPool(poolConfig, URI.create(RpcConfig.REGISTRY_ADDRESS));
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
    protected void subscribe(String serviceName) {
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
