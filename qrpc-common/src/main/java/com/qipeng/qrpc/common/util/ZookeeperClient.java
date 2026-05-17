package com.qipeng.qrpc.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class ZookeeperClient {

    private CuratorFramework client;

    public ZookeeperClient(CuratorFramework client) {
        this.client = client;
        client.start();
    }

    public String createPerNode(String nodePath, String nodeValue) {
        try {
            return client.create().creatingParentsIfNeeded()
                         .forPath(nodePath, nodeValue.getBytes());
        } catch (Exception e) {
            log.error(MessageFormat.format("创建永久Zookeeper节点失败,nodePath:{0},nodeValue:{1}", nodePath, nodeValue), e);
        }
        return null;
    }

    public String createPerNode(String nodePath) {
        try {
            return client.create().creatingParentsIfNeeded().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("创建永久Zookeeper节点失败,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public String createSeqPerNode(String nodePath, String nodeValue) {
        try {
            return client.create().creatingParentsIfNeeded()
                         .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                         .forPath(nodePath, nodeValue.getBytes());
        } catch (Exception e) {
            log.error(MessageFormat.format("创建永久有序Zookeeper节点失败,nodePath:{0},nodeValue:{1}", nodePath, nodeValue), e);
        }
        return null;
    }

    public String createSeqPerNode(String nodePath) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("创建永久有序Zookeeper节点失败,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public String createEphNode(String nodePath, String nodeValue) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, nodeValue.getBytes());
        } catch (Exception e) {
            log.error(MessageFormat.format("创建临时Zookeeper节点失败,nodePath:{0},nodeValue:{1}", nodePath, nodeValue), e);
        }
        return null;
    }

    public String createEphNode(String nodePath) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("创建临时Zookeeper节点失败,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public String createSeqEphNode(String nodePath, String nodeValue) {
        try {
            return client.create().creatingParentsIfNeeded()
                         .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                         .forPath(nodePath, nodeValue.getBytes());
        } catch (Exception e) {
            log.error(MessageFormat.format("创建临时有序Zookeeper节点失败,nodePath:{0},nodeValue:{1}", nodePath, nodeValue), e);
        }
        return null;
    }

    public String createSeqEphNode(String nodePath) {
        try {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("创建临时有序Zookeeper节点失败,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public boolean checkExists(String nodePath) {
        try {
            Stat stat = client.checkExists().forPath(nodePath);
            return stat != null;
        } catch (Exception e) {
            log.error(MessageFormat.format("检查Zookeeper节点是否存在出现异常,nodePath:{0}", nodePath), e);
        }
        return false;
    }

    public List<String> getChildren(String nodePath) {
        try {
            return client.getChildren().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("获取某个Zookeeper节点的所有子节点出现异常,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public String getData(String nodePath) {
        try {
            return new String(client.getData().forPath(nodePath));
        } catch (Exception e) {
            log.error(MessageFormat.format("获取某个Zookeeper节点的数据出现异常,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public void setData(String nodePath, String newNodeValue) {
        try {
            client.setData().forPath(nodePath, newNodeValue.getBytes());
        } catch (Exception e) {
            log.error(MessageFormat.format("设置某个Zookeeper节点的数据出现异常,nodePath:{0}", nodePath), e);
        }
    }

    public void delete(String nodePath) {
        try {
            client.delete().guaranteed().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("删除某个Zookeeper节点出现异常,nodePath:{0}", nodePath), e);
        }
    }

    public void deleteChildrenIfNeeded(String nodePath) {
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format("级联删除某个Zookeeper节点及其子节点出现异常,nodePath:{0}", nodePath), e);
        }
    }

    public CuratorCache registerNodeCacheListener(String nodePath) {
        try {
            CuratorCache curatorCache = CuratorCache.build(client, nodePath);
            curatorCache.listenable().addListener(CuratorCacheListener.builder()
                    .forCreates(childData -> {
                        if (childData != null) {
                            System.out.println("Path: " + childData.getPath());
                            System.out.println("Stat:" + childData.getStat());
                            System.out.println("Data: " + new String(childData.getData()));
                        }
                    })
                    .forChanges((oldNode, node) -> {
                        if (node != null) {
                            System.out.println("Path: " + node.getPath());
                            System.out.println("Stat:" + node.getStat());
                            System.out.println("Data: " + new String(node.getData()));
                        }
                    })
                    .build());
            curatorCache.start();
            return curatorCache;
        } catch (Exception e) {
            log.error(MessageFormat.format("注册节点监听器出现异常,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public CuratorCache registerPathChildListener(String nodePath, PathChildrenCacheListener listener) {
        try {
            CuratorCache curatorCache = CuratorCache.build(client, nodePath);
            curatorCache.listenable().addListener(CuratorCacheListener.builder()
                    .forPathChildrenCache(nodePath, client, listener)
                    .build());
            curatorCache.start();
            return curatorCache;
        } catch (Exception e) {
            log.error(MessageFormat.format("注册子目录监听器出现异常,nodePath:{0}", nodePath), e);
        }
        return null;
    }

    public CuratorCache registerTreeCacheListener(String nodePath, int maxDepth, CuratorCacheListener listener) {
        try {
            CuratorCache curatorCache = CuratorCache.build(client, nodePath);
            curatorCache.listenable().addListener(listener);
            curatorCache.start();
            return curatorCache;
        } catch (Exception e) {
            log.error(MessageFormat.format("注册目录监听器出现异常,nodePath:{0},maxDepth:{1}", nodePath, maxDepth), e);
        }
        return null;
    }
}
