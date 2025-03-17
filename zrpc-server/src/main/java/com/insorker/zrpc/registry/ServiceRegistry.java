package com.insorker.zrpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private final CuratorClient curatorClient;
    private final Set<String> pathSet = new HashSet<>();

    public ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
    }

    public void register(ServiceInfo serviceInfo) {
        try {
            String path = curatorClient.create(
                    String.valueOf(serviceInfo.hashCode()),
                    serviceInfo.toJSONBytes()
            );
            pathSet.add(path);
            logger.info("Register new service on {}:{}", serviceInfo.getHost(), serviceInfo.getPort());
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
