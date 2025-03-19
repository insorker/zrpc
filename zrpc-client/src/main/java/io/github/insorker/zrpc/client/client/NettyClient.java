package io.github.insorker.zrpc.client.client;

import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.proxy.ServiceProxy;
import io.github.insorker.zrpc.client.proxy.ZRpcService;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

public class NettyClient extends ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final int waitForHandlerTimeout = 5000;

    public NettyClient(String registryAddress) {
        super(registryAddress);
    }

    public void stop() {
        super.close();
    }

    public static <T, R> T createService(ServiceInfo serviceInfo, Class<T> cls) {
        return (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class<?>[]{cls},
                new ServiceProxy<T, R>(serviceInfo, cls));
    }

    public static <T, R> ZRpcService<T, R> createAsyncService(ServiceInfo serviceInfo, Class<T> cls) {
        return new ServiceProxy<>(serviceInfo, cls);
    }

    private void waitForHandler() {
        lock.lock();
        try {
            logger.warn("Waiting for available handler");
            connectedCondition.await(waitForHandlerTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public ZRpcClientHandler chooseHandler(ServiceInfo serviceInfo) {
        if (!serviceSocketMap.containsKey(serviceInfo)) {
            throw new ZRpcException("Service not found: " + serviceInfo.toString());
        }

        SocketInfo socketInfo = serviceSocketMap.get(serviceInfo);
        if (!socketHandlerMap.containsKey(socketInfo)) {
            waitForHandler();
            if (!socketHandlerMap.containsKey(socketInfo)) {
                throw new ZRpcException("Handler not fount: " + serviceInfo);
            }
        }

        return socketHandlerMap.get(socketInfo);
    }
}
