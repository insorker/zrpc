package io.github.insorker.zrpc.server;

import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.server.handler.ZRpcServerInitializer;
import io.github.insorker.zrpc.server.registry.ZookeeperRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RegistryServer extends ZookeeperRegistry implements SpringServer {

    private static final Logger logger = LoggerFactory.getLogger(RegistryServer.class);
    private final Map<ServiceInfo, Object> serviceMap = new HashMap<>();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public RegistryServer(String host, int port, String registryAddress) {
        super(registryAddress);
        serverInfo = new ServerInfo(host, port);
    }

    @Override
    public void addService(ServiceInfo serviceInfo, Object serviceBean) {
        logger.info("Add service {}", serviceInfo);

        serverInfo.addService(serviceInfo);
        serviceMap.put(serviceInfo, serviceBean);
        register(serverInfo);
    }

    @Override
    public void removeService(ServiceInfo serviceInfo) {
        logger.info("Remove service {}", serviceInfo);

        if (serviceMap.containsKey(serviceInfo)) {
            serverInfo.removeService(serviceInfo);
            serviceMap.remove(serviceInfo);
            register(serverInfo);
        }
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ZRpcServerInitializer(serviceMap));
        bootstrap.bind(serverInfo.getHost(), serverInfo.getPort());
    }

    @Override
    public void close() {
        super.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
