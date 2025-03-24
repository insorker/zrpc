package io.github.insorker.zrpc.client;

import io.github.insorker.zrpc.client.discovery.ZookeeperDiscovery;
import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.handler.ZRpcClientInitializer;
import io.github.insorker.zrpc.client.proxy.ServiceProxy;
import io.github.insorker.zrpc.client.proxy.ZRpcService;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DiscoveryClient extends ZookeeperDiscovery implements SpringClient {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryClient.class);
    private static final int waitForHandlerTimeout = 5000;
    protected final ReentrantLock connectedLock = new ReentrantLock();
    protected final Condition connectedCondition = connectedLock.newCondition();
    protected final Map<ServiceInfo, SocketInfo> serviceSocketMap = new HashMap<>();
    protected final Map<SocketInfo, ZRpcClientHandler> socketHandlerMap = new HashMap<>();
    private final Map<SocketInfo, EventLoopGroup> socketGroupMap = new HashMap<>();

    public DiscoveryClient(String registryAddress) {
        super(registryAddress);

        try {
            List<ServerInfo> serverInfoList = discover();

            serverInfoList.forEach(serverInfo -> {
                SocketInfo socketInfo = serverInfo.getSocketInfo();
                serverInfo.getServiceInfoList().forEach(serviceInfo ->
                        serviceSocketMap.put(serviceInfo, socketInfo));
                connectServer(socketInfo);
            });
        } catch (Exception e) {
            logger.error("Fail to initialize discovery client: {}", e.toString());
        }
    }

    public <T, R> T createService(ServiceInfo serviceInfo, Class<T> cls) {
        Object object = Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class<?>[]{cls},
                new ServiceProxy<T, R>(serviceInfo, cls));

        if (cls.isInstance(object)) {
            return cls.cast(object);
        }

        throw new ZRpcException("Proxy error");
    }

    public <T, R> ZRpcService<T, R> createAsyncService(ServiceInfo serviceInfo, Class<T> cls) {
        return new ServiceProxy<>(serviceInfo, cls);
    }

    public ZRpcClientHandler chooseHandler(ServiceInfo serviceInfo) {
        if (!serviceSocketMap.containsKey(serviceInfo)) {
            throw new ZRpcException("Handler not found: " + serviceInfo);
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

    @Override
    public void close() {
        super.close();
        socketGroupMap.values().forEach(EventLoopGroup::shutdownGracefully);
    }

    @Override
    protected void updateNodeCreated(ServerInfo serverInfo) {
        // Add services to map
        serverInfo.getServiceInfoList().forEach(serviceInfo -> {
            serviceSocketMap.putIfAbsent(serviceInfo, serverInfo.getSocketInfo());
        });

        // Connect to server if not connected
        connectServer(serverInfo.getSocketInfo());
    }

    @Override
    protected void updateNodeChanged(ServerInfo newServerInfo, ServerInfo oldServerInfo) {
        List<ServiceInfo> newServiceInfoList = newServerInfo.getServiceInfoList();
        List<ServiceInfo> oldServiceInfoList = oldServerInfo.getServiceInfoList();
        SocketInfo newSocketInfo = newServerInfo.getSocketInfo();
        SocketInfo oldSocketInfo = oldServerInfo.getSocketInfo();

        oldServiceInfoList.forEach(serviceSocketMap::remove);
        newServiceInfoList.forEach(serviceInfo ->
            serviceSocketMap.put(serviceInfo, newServerInfo.getSocketInfo()));

        if (!newSocketInfo.equals(oldSocketInfo)) {
            disconnectServer(oldSocketInfo);
            connectServer(newSocketInfo);
        }
    }

    @Override
    protected void updateNodeDeleted(ServerInfo serverInfo) {
        serverInfo.getServiceInfoList().forEach(serviceSocketMap::remove);
        disconnectServer(serverInfo.getSocketInfo());
    }

    private void waitForHandler() {
        connectedLock.lock();
        try {
            logger.info("Waiting for available handler");
            if (!connectedCondition.await(waitForHandlerTimeout, TimeUnit.MILLISECONDS)) {
                throw new Exception("Get handler timeout");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            connectedLock.unlock();
        }
    }

    private void connectServer(SocketInfo socketInfo) {
        if (isConnectedServer(socketInfo)) {
            return;
        }

        logger.info("Try to connect to server {} ...", socketInfo);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        socketGroupMap.put(socketInfo, group);
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ZRpcClientInitializer());

        ChannelFuture connect = b.connect(socketInfo.getHost(), socketInfo.getPort());
        connect.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                ZRpcClientHandler handler = channelFuture.channel().pipeline().get(ZRpcClientHandler.class);

                // Add to socketHandlerMap
                socketHandlerMap.put(socketInfo, handler);

                // Signal waitForHandler
                connectedLock.lock();
                try {
                    connectedCondition.signalAll();
                } finally {
                    connectedLock.unlock();
                }

                logger.info("Connect to server {}.", socketInfo);
            }
            else {
                logger.error("Cannot connect to server {}.", socketInfo);
            }
        });
    }

    private void disconnectServer(SocketInfo socketInfo) {
        if (isConnectedServer(socketInfo)) {
            return;
        }

        logger.info("Disconnect from server {} ...", socketInfo);

        ZRpcClientHandler handler = socketHandlerMap.get(socketInfo);
        EventLoopGroup group = socketGroupMap.get(socketInfo);

        socketHandlerMap.remove(socketInfo);
        socketGroupMap.remove(socketInfo);
        handler.getChannel().close();
        group.shutdownGracefully();
    }

    private boolean isConnectedServer(SocketInfo socketInfo) {
        return socketHandlerMap.containsKey(socketInfo) && socketGroupMap.containsKey(socketInfo);
    }
}
