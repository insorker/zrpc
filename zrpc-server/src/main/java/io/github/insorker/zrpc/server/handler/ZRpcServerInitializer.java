package io.github.insorker.zrpc.server.handler;

import io.github.insorker.zrpc.common.codec.ZRpcBeat;
import io.github.insorker.zrpc.common.codec.ZRpcDecoder;
import io.github.insorker.zrpc.common.codec.ZRpcEncoder;
import io.github.insorker.zrpc.common.protocol.ZRpcProtocolFactory;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.github.insorker.zrpc.common.protocol.json.JsonProtocolFactory;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ZRpcServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ZRpcProtocolFactory protocolFactory;
    private final Map<ServiceInfo, Object> serviceMap;

    public ZRpcServerInitializer(Map<ServiceInfo, Object> serviceMap, ZRpcProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
        this.serviceMap = serviceMap;
    }

    public ZRpcServerInitializer(Map<ServiceInfo, Object> serviceMap) {
        this(serviceMap, new JsonProtocolFactory());
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // ZRpcDecoder is not a @Sharable handler, so can't be added or removed multiple times.
        // Create when using rather than reusing.
        ZRpcDecoder<ZRpcRequest> decoder = new ZRpcDecoder<>(protocolFactory.createProtocol(), ZRpcRequest.class);
        ZRpcEncoder<ZRpcResponse> encoder = new ZRpcEncoder<>(protocolFactory.createProtocol(), ZRpcResponse.class);

        socketChannel.pipeline()
                .addLast(new IdleStateHandler(0, 0, ZRpcBeat.BEAT_TIMEOUT, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(new ZRpcServerHandler(serviceMap));
    }
}
