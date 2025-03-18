package io.github.insorker.client.client;

import io.github.insorker.zrpc.common.registry.CuratorClient;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private final CuratorClient curatorClient;
    private final Map<ServiceInfo, SocketInfo> serviceInfoMap = new HashMap<>();

    public ServiceDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);

        try {
            List<String> nodeList = curatorClient.getChildren(CuratorClient.ZK_SERVICE_PATH);

            for (String node : nodeList) {
                byte[] data = curatorClient.getData(CuratorClient.ZK_SERVICE_PATH + "/" + node);
                updateServiceInfoMap(ServerInfo.parseObject(data));
            }

            curatorClient.watchChildren(CuratorClient.ZK_SERVICE_PATH, (type, oldData, newData) -> {
                ServerInfo serverInfo = ServerInfo.parseObject(newData.getData());

                if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                    updateNodeCreated(serverInfo);
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                    updateNodeChanged(serverInfo);
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_DELETED.name())) {
                    updateNodeDeleted(serverInfo);
                }
            });
        } catch (Exception e) {
            logger.error(" Initialize discovery error: ", e);
        }
    }

    protected void updateServiceInfoMap(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceInfo -> {
            serviceInfoMap.put(serviceInfo, serverInfo.getSocketInfo());
        });
    }

    protected void updateNodeCreated(ServerInfo serverInfo) {
        updateServiceInfoMap(serverInfo);
    }

    protected void updateNodeChanged(ServerInfo serverInfo) {
        updateServiceInfoMap(serverInfo);
    }

    protected void updateNodeDeleted(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceInfoMap::remove);
    }

    public void close() {
        curatorClient.close();
    }
}
