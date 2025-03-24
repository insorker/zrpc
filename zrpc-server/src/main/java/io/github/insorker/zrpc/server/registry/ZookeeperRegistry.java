package io.github.insorker.zrpc.server.registry;

import io.github.insorker.zrpc.common.registry.CuratorClient;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperRegistry extends ServerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private final CuratorClient curatorClient;
    protected ServerInfo serverInfo;

    public ZookeeperRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);

        // Listen to registry's connection state
        curatorClient.addConnectionStateListener(((curatorFramework, connectionState) -> {
            if (connectionState == ConnectionState.RECONNECTED) {
                logger.info("Reconnect to zookeeper ...");

                if (serverInfo != null) {
                    logger.info("Register server {}", serverInfo);
                    register(serverInfo);
                }
            }
        }));
    }

    @Override
    public void register(ServerInfo serverInfo) {
        if (this.serverInfo != null) {
            unregister(serverInfo);
        }
        this.serverInfo = serverInfo;

        try {
            String path = serverInfo.getPath();
            byte[] data = serverInfo.toJSONBytes();

            if (curatorClient.exist(path)) {
                curatorClient.setData(path, data);
            }
            else {
                curatorClient.create(path, data);
            }

            logger.info("Register server {}", serverInfo);
        } catch (Exception e) {
            logger.error("Fail to register server, exception: {}", e.getMessage());
        }
    }

    @Override
    public void unregister(ServerInfo serverInfo) {
        try {
            curatorClient.remove(serverInfo.getPath());

            logger.info("Unregister server {}", serverInfo);
        } catch (Exception e) {
            logger.error("Fail to unregister server, exception: {}", e.getMessage());
        }
    }

    @Override
    public void close() {
        unregister(serverInfo);
        curatorClient.close();
    }
}
