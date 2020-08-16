package com.qipeng.qrpc.common.registry.impl;

import com.qipeng.qrpc.common.RpcConfig;
import com.qipeng.qrpc.common.ServerParam;
import com.qipeng.qrpc.common.exception.RpcException;
import com.qipeng.qrpc.common.registry.Registry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisRegistry implements Registry {

    private volatile static RedisRegistry instance;

    private JedisPool jedisPool;

    private RedisRegistry() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
        poolConfig.setMaxIdle(32);
        poolConfig.setMaxWaitMillis(100 * 1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, URI.create(RpcConfig.REGISTRY_ADDRESS));
    }

    public static RedisRegistry getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (RedisRegistry.class) {
            if (instance == null) {
                instance = new RedisRegistry();
            }
            return instance;
        }
    }

    @Override
    public List<ServerParam> getServerParam(String serviceName) {
        Set<String> servers;
        try (Jedis jedis = jedisPool.getResource()) {
            servers = jedis.smembers("/qrpc/" + serviceName);
        }
        if (servers == null || servers.isEmpty()) {
            throw new RpcException();
        }
        return servers.stream()
                .map(s -> s.split(":"))
                .map(s -> new ServerParam(s[0], Integer.parseInt(s[1])))
                .collect(Collectors.toList());
    }

    @Override
    public boolean registerService(String serviceName, ServerParam serverParam) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("/qrpc/" + serviceName, serverParam.getHost() + ":" + serverParam.getPort());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
