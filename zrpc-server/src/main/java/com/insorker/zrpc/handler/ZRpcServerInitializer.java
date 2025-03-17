package com.insorker.zrpc.handler;

import com.insorker.zrpc.codec.ZRpcDecoder;
import com.insorker.zrpc.codec.ZRpcEncoder;
import com.insorker.zrpc.protocol.JsonProtocol;
import com.insorker.zrpc.protocol.ZRpcRequest;
import com.insorker.zrpc.protocol.ZRpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                .addLast(decoder)
                .addLast(encoder)
                .addLast(new ZRpcServerHandler());
    }
}
