package io.github.insorker.zrpc.client.handler;

import io.github.insorker.zrpc.common.codec.ZRpcBeat;
import io.github.insorker.zrpc.common.codec.ZRpcDecoder;
import io.github.insorker.zrpc.common.codec.ZRpcEncoder;
import io.github.insorker.zrpc.common.protocol.ZRpcProtocolFactory;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.github.insorker.zrpc.common.protocol.json.JsonProtocolFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ZRpcClientInitializer extends ChannelInitializer<SocketChannel> {

    private final ZRpcProtocolFactory protocolFactory;

    public ZRpcClientInitializer(ZRpcProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public ZRpcClientInitializer() {
        this(new JsonProtocolFactory());
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // ZRpcDecoder is not a @Sharable handler, so can't be added or removed multiple times.
        // Create when using rather than reusing.
        ZRpcDecoder<ZRpcResponse> decoder = new ZRpcDecoder<>(protocolFactory.createProtocol(), ZRpcResponse.class);
        ZRpcEncoder<ZRpcRequest> encoder = new ZRpcEncoder<>(protocolFactory.createProtocol(), ZRpcRequest.class);

        socketChannel.pipeline()
                .addLast(new IdleStateHandler(0, 0, ZRpcBeat.BEAT_TIMEOUT, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(new ZRpcClientHandler());

    }
}
