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
    protected final Map<ServiceInfo, SocketInfo> serviceSocketMap = new HashMap<>();
    private final CuratorClient curatorClient;

    public ServiceDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);

        try {
            List<String> nodeList = curatorClient.getChildren(CuratorClient.ZK_SERVICE_PATH);

            for (String node : nodeList) {
                byte[] data = curatorClient.getData(CuratorClient.ZK_SERVICE_PATH + "/" + node);
                initServer(ServerInfo.parseObject(data));
            }

            curatorClient.watchChildren(CuratorClient.ZK_SERVICE_PATH, (type, oldData, newData) -> {
                ServerInfo oldServerInfo = ServerInfo.parseObject(oldData.getData());
                ServerInfo newServerInfo = ServerInfo.parseObject(newData.getData());

                if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                    updateNodeCreated(newServerInfo);
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                    updateNodeChanged(newServerInfo, oldServerInfo);
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_DELETED.name())) {
                    updateNodeDeleted(newServerInfo);
                }
            });
        } catch (Exception e) {
            logger.error(" Initialize discovery error: ", e);
        }
    }

    public void close() {
        curatorClient.close();
    }

    protected void initServer(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceInfo -> {
            serviceSocketMap.put(serviceInfo, serverInfo.getSocketInfo());
        });
    }

    protected void updateNodeCreated(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceInfo -> {
            serviceSocketMap.putIfAbsent(serviceInfo, serverInfo.getSocketInfo());
        });
    }

    protected void updateNodeChanged(ServerInfo newServerInfo, ServerInfo oldServerInfo) {
        List<ServiceInfo> newServiceInfoList = newServerInfo.getServiceInfoList();
        List<ServiceInfo> oldServiceInfoList = oldServerInfo.getServiceInfoList();

        oldServiceInfoList.forEach(serviceInfo -> {
            if (!newServiceInfoList.contains(serviceInfo)) {
                serviceSocketMap.remove(serviceInfo);
            }
        });
        newServiceInfoList.forEach(serviceInfo -> {
            serviceSocketMap.put(serviceInfo, newServerInfo.getSocketInfo());
        });
    }

    protected void updateNodeDeleted(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceSocketMap::remove);
    }
}
