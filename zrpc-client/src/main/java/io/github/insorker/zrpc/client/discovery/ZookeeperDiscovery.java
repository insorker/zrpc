package io.github.insorker.zrpc.client.discovery;

import io.github.insorker.zrpc.common.registry.CuratorClient;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperDiscovery extends Discovery {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperDiscovery.class);
    private final CuratorClient curatorClient;

    public ZookeeperDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);

        curatorClient.watchChildren("", (type, oldData, newData) -> {
            ServerInfo oldServerInfo = oldData != null ? ServerInfo.parseObject(oldData.getData()) : null;
            ServerInfo newServerInfo = newData != null ? ServerInfo.parseObject(newData.getData()) : null;

            logger.info("Zookeeper node changed, type {}, newServerInfo: {}, oldServerInfo {}", type.name(), newServerInfo, oldServerInfo);

            if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                if (newServerInfo != null) {
                    updateNodeCreated(newServerInfo);
                }
            }
            else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                if (oldServerInfo != null && newServerInfo != null) {
                    updateNodeChanged(newServerInfo, oldServerInfo);
                }
            }
            else if (type.name().equals(CuratorCacheListener.Type.NODE_DELETED.name())) {
                if (newServerInfo != null) {
                    updateNodeDeleted(newServerInfo);
                }
            }
        });
    }

    @Override
    public List<ServerInfo> discover() throws Exception {
        List<String> nodeList = curatorClient.getChildren("");
        List<ServerInfo> serverInfoList = new ArrayList<>();

        for (String node : nodeList) {
            byte[] data = curatorClient.getData(node);
            ServerInfo serverInfo = ServerInfo.parseObject(data);
            serverInfoList.add(serverInfo);
        }

        return serverInfoList;
    }

    @Override
    public void close() {
        curatorClient.close();
    }

    protected void updateNodeCreated(ServerInfo serverInfo) { }

    protected void updateNodeChanged(ServerInfo newServerInfo, ServerInfo oldServerInfo) { }

    protected void updateNodeDeleted(ServerInfo serverInfo) { }
}
