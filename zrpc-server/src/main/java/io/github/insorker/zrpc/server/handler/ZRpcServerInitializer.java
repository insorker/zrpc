package io.github.insorker.zrpc.server.handler;

import io.github.insorker.zrpc.common.codec.ZRpcBeat;
import io.github.insorker.zrpc.common.codec.ZRpcDecoder;
import io.github.insorker.zrpc.common.codec.ZRpcEncoder;
import io.github.insorker.zrpc.common.protocol.JsonProtocol;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ZRpcServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ZRpcDecoder<ZRpcRequest> decoder;
    private final ZRpcEncoder<ZRpcResponse> encoder;

    public ZRpcServerInitializer(ZRpcDecoder<ZRpcRequest> decoder, ZRpcEncoder<ZRpcResponse> encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public ZRpcServerInitializer() {
        this(new ZRpcDecoder<>(new JsonProtocol<>(), ZRpcRequest.class),
                new ZRpcEncoder<>(new JsonProtocol<>(), ZRpcResponse.class));
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
                .addLast(new IdleStateHandler(0, 0, ZRpcBeat.BEAT_TIMEOUT, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(new ZRpcServerHandler());
    }
}
