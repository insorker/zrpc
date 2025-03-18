package io.github.insorker.zrpc.server.server;

import io.github.insorker.zrpc.common.registry.CuratorClient;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private final CuratorClient curatorClient;
    private final Set<String> pathSet = new HashSet<>();
    private ServerInfo serverInfo = null;

    public ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);

        curatorClient.addConnectionStateListener(((curatorFramework, connectionState) -> {
            if (connectionState == ConnectionState.RECONNECTED && serverInfo != null) {
                logger.info("Reconnect to registry...");
                logger.info("Register new service on {}:{}", serverInfo.getHost(), serverInfo.getPort());
                register(serverInfo);
            }
        }));
    }

    public void register(ServerInfo serverInfo) {
        try {
            String path = String.valueOf(serverInfo.getSocketInfo().hashCode());
            byte[] data = serverInfo.toJSONBytes();

            pathSet.add(path);
            this.serverInfo = serverInfo;
            if (curatorClient.exist(path)) {
                curatorClient.setData(path, data);
            }
            else {
                curatorClient.create(path, data);
            }

            logger.info("Register new service on {}:{}", serverInfo.getHost(), serverInfo.getPort());
        } catch (Exception e) {
            logger.error("Fail to register service, exception: {}", e.getMessage());
        }
    }

    public void unregister(String path) {
        logger.info("Unregister service: " + path);
        try {
            curatorClient.remove(path);
        } catch (Exception e) {
            logger.error("Fail to unregister service, exception: {}", e.getMessage());
        }
    }

    public void close() {
        logger.info("Unregister all services");
        pathSet.forEach(this::unregister);
        curatorClient.close();
    }
}
