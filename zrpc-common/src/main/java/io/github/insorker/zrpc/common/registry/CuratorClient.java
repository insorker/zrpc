package io.github.insorker.zrpc.common.registry;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.spi.ServiceRegistry;

public final class CuratorClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    static int ZK_SESSION_TIMEOUT = 5000;
    static int ZK_CONNECTION_TIMEOUT = 5000;
    static String ZK_NAMESPACE = "zrpc";
    static String ZK_REGISTRY_PATH = "/registry";
    static String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";

    private final CuratorFramework client;

    public CuratorClient(String connectString, String namespace, int sessionTimeout, int connectTimeout, RetryPolicy retryPolicy) {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectTimeout)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    public CuratorClient(String connectString) {
        this(connectString, ZK_NAMESPACE, ZK_SESSION_TIMEOUT, ZK_CONNECTION_TIMEOUT, new ExponentialBackoffRetry(1000, 10));
    }

    public String create(String path, byte[] data) throws Exception {
        return client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(ZK_DATA_PATH + "/" + path, data);
    }

    public void remove(String path) throws Exception {
        client.delete().forPath(path);
    }

    public byte[] get(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
