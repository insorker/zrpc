package io.github.insorker.zrpc.common.registry;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public final class CuratorClient {

    public static int ZK_SESSION_TIMEOUT = 5000;
    public static int ZK_CONNECTION_TIMEOUT = 5000;
    public static String ZK_NAMESPACE = "zrpc";
    public static String ZK_SERVICE_PATH = "/service";

    private final CuratorFramework client;

    public CuratorClient(String registryAddress, String namespace, int sessionTimeout, int connectTimeout, RetryPolicy retryPolicy) {
        client = CuratorFrameworkFactory.builder()
                .connectString(registryAddress)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectTimeout)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    public CuratorClient(String registryAddress) {
        this( registryAddress, ZK_NAMESPACE, ZK_SESSION_TIMEOUT, ZK_CONNECTION_TIMEOUT, new ExponentialBackoffRetry(1000, 10));
    }

    public String create(String path, byte[] data) throws Exception {
        return client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(ZK_SERVICE_PATH + "/" + path, data);
    }

    public void remove(String path) throws Exception {
        client.delete().forPath(path);
    }

    public boolean exist(String path) throws Exception {
        return client.checkExists().forPath(path) != null;
    }

    public void setData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public void watchChildren(String path, CuratorCacheListener listener) {
        CuratorCache curatorCache = CuratorCache.build(client, path);
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
