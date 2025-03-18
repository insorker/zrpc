package io.github.insorker.zrpc.client.client;

import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;

public class NettyClient extends ServiceDiscovery {

    public NettyClient(String registryAddress) {
        super(registryAddress);
    }

    public void stop() {
        super.close();
    }

    public ZRpcClientHandler chooseHandler(ServiceInfo serviceInfo) {
        SocketInfo socketInfo = serviceSocketMap.get(serviceInfo);
        return socketHandlerMap.get(socketInfo);
    }
}
