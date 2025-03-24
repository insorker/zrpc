package io.github.insorker.zrpc.client;

import io.github.insorker.zrpc.common.exceptions.ZRpcException;

public class SingletonDiscoveryClient extends DiscoveryClient {

    private static volatile SingletonDiscoveryClient instance;

    public static SingletonDiscoveryClient newInstance(String registryAddress) {
        if (instance == null) {
            synchronized (SingletonDiscoveryClient.class) {
                if (instance == null) {
                    instance = new SingletonDiscoveryClient(registryAddress);
                }
            }
        }
        return instance;
    }

    public static SingletonDiscoveryClient getInstance() {
        if (instance == null) {
            throw new ZRpcException("ZRpcClient has not been built");
        }
        return instance;
    }

    private SingletonDiscoveryClient(String registryAddress) {
        super(registryAddress);
    }
}
