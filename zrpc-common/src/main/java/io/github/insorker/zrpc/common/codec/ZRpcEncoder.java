package io.github.insorker.zrpc.common.codec;

import io.github.insorker.zrpc.common.protocol.ZRpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZRpcEncoder<T> extends MessageToByteEncoder<T> {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcDecoder.class);
    private final ZRpcProtocol<T> protocol;
    private final Class<T> clazz;

    public ZRpcEncoder(ZRpcProtocol<T> protocol, Class<T> clazz) {
        // preventing generic type erasure
        super(clazz);

        this.protocol = protocol;
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) {
        try {
            protocol.encode(obj, clazz, byteBuf);
        } catch (Exception e) {
            logger.error("Encode error: " + e);
        }
    }
}
