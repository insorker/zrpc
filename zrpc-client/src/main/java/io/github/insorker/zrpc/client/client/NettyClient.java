package io.github.insorker.zrpc.client.client;

import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.proxy.ServiceProxy;
import io.github.insorker.zrpc.client.proxy.ZRpcService;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;

import java.lang.reflect.Proxy;

public class NettyClient extends ServiceDiscovery {

    public NettyClient(String registryAddress) {
        super(registryAddress);
    }

    public void stop() {
        super.close();
    }

    public static <T, R> T createService(Class<T> cls) {
        return (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class<?>[]{cls},
                new ServiceProxy<T, R>(cls));
    }

    public static <T, R> ZRpcService<T, R> createAsyncService(Class<T> cls) {
        return new ServiceProxy<>(cls);
    }

    public ZRpcClientHandler chooseHandler(ServiceInfo serviceInfo) {
        SocketInfo socketInfo = serviceSocketMap.get(serviceInfo);
        return socketHandlerMap.get(socketInfo);
    }
}
