package io.github.insorker.zrpc.server.server;

import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.server.handler.ZRpcServerInitializer;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final ServerInfo serverInfo;
    private final ServiceRegistry serviceRegistry;
    private final Map<ServiceInfo, Object> serviceMap = new HashMap<>();

    public NettyServer(String host, int port, ServiceRegistry serviceRegistry) {
        this.serverInfo = new ServerInfo(host, port);
        this.serviceRegistry = serviceRegistry;
    }

    public void addService(ServiceInfo serviceInfo, Object serviceBean) {
        logger.info("Add service {}", serviceInfo);

        serverInfo.addService(serviceInfo);
        serviceMap.put(serviceInfo, serviceBean);
        serviceRegistry.register(serverInfo);
    }

    public Set<ServiceInfo> getServices() {
        return serviceMap.keySet();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ZRpcServerInitializer());
        bootstrap.bind(serverInfo.getHost(), serverInfo.getPort());
    }

    public void stop() {
        serviceRegistry.close();
    }
}
