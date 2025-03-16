package com.insorker.zrpc.codec;

import com.insorker.zrpc.protocol.ZRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ZRpcDecoder<T> extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcDecoder.class);
    private final ZRpcProtocol<T> protocol;
    private final Class<T> clazz;

    public ZRpcDecoder(ZRpcProtocol<T> protocol, Class<T> clazz) {
        this.protocol = protocol;
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        try {
            T obj = protocol.decode(byteBuf, clazz);
            Optional.ofNullable(obj).ifPresent(list::add);
        } catch (Exception e) {
            logger.error("Decode error: " + e);
        }
    }
}
