package io.github.insorker.zrpc.client.client;

import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.handler.ZRpcClientInitializer;
import io.github.insorker.zrpc.common.registry.CuratorClient;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    protected final Map<ServiceInfo, SocketInfo> serviceSocketMap = new HashMap<>();
    protected final Map<SocketInfo, ZRpcClientHandler> socketHandlerMap = new HashMap<>();
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Condition connectedCondition = lock.newCondition();
    private final CuratorClient curatorClient;
    private final Set<EventLoopGroup> groups = new HashSet<>();

    public ServiceDiscovery(String registryAddress) {
        this.curatorClient = new CuratorClient(registryAddress);

        try {
            List<String> nodeList = curatorClient.getChildren("");

            for (String node : nodeList) {
                byte[] data = curatorClient.getData(node);
                ServerInfo serverInfo = ServerInfo.parseObject(data);
                serverInfo.getServiceInfoList().forEach(serviceInfo -> {
                    serviceSocketMap.put(serviceInfo, serverInfo.getSocketInfo());
                });
                connectServer(serverInfo.getSocketInfo());
            }

            curatorClient.watchChildren("", (type, oldData, newData) -> {
                ServerInfo oldServerInfo = oldData != null ? ServerInfo.parseObject(oldData.getData()) : null;
                ServerInfo newServerInfo = newData != null ? ServerInfo.parseObject(newData.getData()) : null;

                if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                    if (newServerInfo != null) {
                        updateNodeCreated(newServerInfo);
                    }
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                    if (oldServerInfo != null && newServerInfo != null) {
                        updateNodeChanged(newServerInfo, oldServerInfo);
                    }
                }
                else if (type.name().equals(CuratorCacheListener.Type.NODE_DELETED.name())) {
                    if (newServerInfo != null) {
                        updateNodeDeleted(newServerInfo);
                    }
                }
            });
        } catch (Exception e) {
            logger.error(" Initialize discovery error: ", e);
        }
    }

    public void close() {
        curatorClient.close();
        groups.forEach(EventLoopGroup::shutdownGracefully);
    }

    protected void updateNodeCreated(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceInfo -> {
            serviceSocketMap.putIfAbsent(serviceInfo, serverInfo.getSocketInfo());
        });
        if (!socketHandlerMap.containsKey(serverInfo.getSocketInfo())) {
            connectServer(serverInfo.getSocketInfo());
        }
    }

    protected void updateNodeChanged(ServerInfo newServerInfo, ServerInfo oldServerInfo) {
        List<ServiceInfo> newServiceInfoList = newServerInfo.getServiceInfoList();
        List<ServiceInfo> oldServiceInfoList = oldServerInfo.getServiceInfoList();
        SocketInfo newSocketInfo = newServerInfo.getSocketInfo();
        SocketInfo oldSocketInfo = oldServerInfo.getSocketInfo();

        oldServiceInfoList.forEach(serviceInfo -> {
            if (!newServiceInfoList.contains(serviceInfo)) {
                serviceSocketMap.remove(serviceInfo);
            }
        });
        newServiceInfoList.forEach(serviceInfo -> {
            serviceSocketMap.put(serviceInfo, newServerInfo.getSocketInfo());
        });

        if (!newSocketInfo.equals(oldSocketInfo)) {
            socketHandlerMap.remove(oldSocketInfo);
            connectServer(newSocketInfo);
        }
    }

    protected void updateNodeDeleted(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceSocketMap::remove);
        socketHandlerMap.remove(serverInfo.getSocketInfo());
    }

    private void connectServer(SocketInfo socketInfo) {
        logger.info("Try to connect to server {} ...", socketInfo);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        groups.add(group);
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ZRpcClientInitializer());

        ChannelFuture connect = b.connect(socketInfo.getHost(), socketInfo.getPort());
        connect.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                ZRpcClientHandler handler = channelFuture.channel().pipeline().get(ZRpcClientHandler.class);
                socketHandlerMap.put(socketInfo, handler);
                signalAll();

                logger.info("Connect to server {}.", socketInfo);
            }
            else {
                logger.error("Cannot connect to server {}.", socketInfo);
            }
        });
    }

    private void signalAll() {
        lock.lock();
        try {
            connectedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
