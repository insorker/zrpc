package io.github.insorker.client.handler;

import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ZRpcClientHandler extends SimpleChannelInboundHandler<ZRpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ZRpcResponse response) throws Exception {
        System.out.println(response);
    }
}
