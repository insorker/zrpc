package com.insorker.zrpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private final CuratorClient curatorClient;
    private final List<String> pathList = new ArrayList<>();

    private ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
    }

    public void register(String host, int port) {
        try {
            String path = curatorClient.create(
                    "",
                    null
            );
            pathList.add(path);
            logger.info("Register new service on {}:{}", host, port);
        } catch (Exception e) {
            logger.error("Fail to register service, exception: {}", e.getMessage());
        }
    }

    public void unregister(String path) {
        try {
            curatorClient.remove(path);
        } catch (Exception e) {
            logger.error("Fail to unregister service, exception: {}", e.getMessage());
        }
    }

    public void close() {
        curatorClient.close();
    }
}
