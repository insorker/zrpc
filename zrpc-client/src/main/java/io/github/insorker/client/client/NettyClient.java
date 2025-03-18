package io.github.insorker.client.client;

import io.github.insorker.client.handler.ZRpcClientInitializer;
import io.github.insorker.zrpc.common.registry.ServerInfo;
import io.github.insorker.zrpc.common.registry.SocketInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient extends ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    public NettyClient(String registryAddress) {
        super(registryAddress);
    }

    @Override
    protected void updateNodeCreated(ServerInfo serverInfo) {
    }

    @Override
    protected void updateNodeChanged(ServerInfo serverInfo) {
    }

    @Override
    protected void updateNodeDeleted(ServerInfo serverInfo) {
    }

    public void start(SocketInfo socketInfo) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ZRpcClientInitializer());
        b.connect(socketInfo.getHost(), socketInfo.getPort());
    }

    public void stop() {
        super.close();
    }
}
