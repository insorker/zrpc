package io.github.insorker.client.client;

import io.github.insorker.client.handler.ZRpcClientHandler;
import io.github.insorker.client.handler.ZRpcClientInitializer;
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

import java.util.HashMap;
import java.util.Map;

public class NettyClient extends ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    protected final Map<SocketInfo, ZRpcClientHandler> socketHandlerMap = new HashMap<>();
    protected final Map<SocketInfo, EventLoopGroup> socketGroupMap = new HashMap<>();

    public NettyClient(String registryAddress) {
        super(registryAddress);
    }

    @Override
    protected void initServer(ServerInfo serverInfo) {
        super.initServer(serverInfo);
        connectServer(serverInfo.getSocketInfo());
    }

    public void stop() {
        super.close();
        socketGroupMap.values().forEach(EventLoopGroup::shutdownGracefully);
    }

    public ZRpcClientHandler chooseHandler(ServiceInfo serviceInfo) {
        SocketInfo socketInfo = serviceSocketMap.get(serviceInfo);
        return socketHandlerMap.get(socketInfo);
    }

    @Override
    protected void updateNodeCreated(ServerInfo serverInfo) {
        super.updateNodeCreated(serverInfo);

        if (!socketHandlerMap.containsKey(serverInfo.getSocketInfo())) {
            connectServer(serverInfo.getSocketInfo());
        }
    }

    @Override
    protected void updateNodeChanged(ServerInfo newServerInfo, ServerInfo oldServerInfo) {
        super.updateNodeChanged(newServerInfo, oldServerInfo);

        SocketInfo newSocketInfo = newServerInfo.getSocketInfo();
        SocketInfo oldSocketInfo = oldServerInfo.getSocketInfo();

        if (!newSocketInfo.equals(oldSocketInfo)) {
            socketHandlerMap.remove(oldSocketInfo);
            socketGroupMap.get(oldSocketInfo).shutdownGracefully();
            socketGroupMap.remove(oldSocketInfo);

            connectServer(newSocketInfo);
        }
    }

    @Override
    protected void updateNodeDeleted(ServerInfo serverInfo) {
        super.updateNodeDeleted(serverInfo);

        socketHandlerMap.remove(serverInfo.getSocketInfo());
    }

    private void connectServer(SocketInfo socketInfo) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ZRpcClientInitializer());

        ChannelFuture connect = b.connect(socketInfo.getHost(), socketInfo.getPort());
        connect.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                ZRpcClientHandler handler = channelFuture.channel().pipeline().get(ZRpcClientHandler.class);
                socketHandlerMap.put(socketInfo, handler);
                socketGroupMap.put(socketInfo, group);
            }
            else {
                logger.error("Cannot connect to server {}.", socketInfo);
            }
        });
    }
}
