package io.github.insorker.zrpc.server.registry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.junit.Test;

public class ZRpcServerTest {

    @Test
    public void testEncoder() {
        class TestEncoder<T> extends MessageToByteEncoder<T> {

            public TestEncoder() {
                // 泛型擦除，没有这行会报错
                super((Class<? extends T>) String.class);
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, T t, ByteBuf byteBuf) throws Exception {

            }
        }

        TestEncoder<String> testEncoder = new TestEncoder<>();
    }
}
