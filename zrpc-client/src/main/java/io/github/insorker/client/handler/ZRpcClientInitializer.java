package io.github.insorker.client.handler;

import io.github.insorker.zrpc.common.codec.ZRpcDecoder;
import io.github.insorker.zrpc.common.codec.ZRpcEncoder;
import io.github.insorker.zrpc.common.protocol.JsonProtocol;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ZRpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private final ZRpcDecoder<ZRpcResponse> decoder;
    private final ZRpcEncoder<ZRpcRequest> encoder;

    public ZRpcClientInitializer(ZRpcDecoder<ZRpcResponse> decoder, ZRpcEncoder<ZRpcRequest> encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public ZRpcClientInitializer() {
        this(new ZRpcDecoder<>(new JsonProtocol<>(), ZRpcResponse.class),
                new ZRpcEncoder<>(new JsonProtocol<>(), ZRpcRequest.class));
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(new ZRpcClientHandler());

    }
}
