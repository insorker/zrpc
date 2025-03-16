package com.insorker.zrpc.protocol;

import com.insorker.zrpc.exceptions.ZRpcException;
import io.netty.buffer.ByteBuf;

public abstract class ZRpcProtocol<T> {

    public static final int HEADER_LENGTH = 4;

    public abstract byte[] serialize(Object obj);

    public abstract T deserialize(byte[] bytes, Class<T> cls);

    /**
     * Encode Object to BytBuf.
     */
    public void encode(Object obj, Class<T> cls, ByteBuf byteBuf) {
        if (cls.isInstance(obj)) {
            byte[] data = serialize(obj);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }

    /**
     * Decode ByteBuf, return null if failed.
     */
    public T decode(ByteBuf byteBuf, Class<T> cls) throws ZRpcException {
        // check protocol header
        if (byteBuf.readableBytes() < HEADER_LENGTH) {
            return null;
        }

        byteBuf.markReaderIndex();

        // check protocol data
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return null;
        }

        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        return deserialize(data, cls);
    }
}
