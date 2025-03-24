package io.github.insorker.zrpc.server;

import io.github.insorker.zrpc.common.registry.ServiceInfo;

public interface ZRpcServer {

    void addService(ServiceInfo serviceInfo, Object serviceBean);

    void removeService(ServiceInfo serviceInfo);

    void start();

    void close();
}
