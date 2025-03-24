package io.github.insorker.zrpc.client;

import io.github.insorker.zrpc.client.proxy.ZRpcService;
import io.github.insorker.zrpc.common.registry.ServiceInfo;

public interface ZRpcClient {

    <T, R> T createService(ServiceInfo serviceInfo, Class<T> cls);
    <T, R> ZRpcService<T, R> createAsyncService(ServiceInfo serviceInfo, Class<T> cls);

    void start();

    void close();
}
