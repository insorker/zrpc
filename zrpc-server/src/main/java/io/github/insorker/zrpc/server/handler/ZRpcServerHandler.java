package io.github.insorker.zrpc.server.handler;

import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ZRpcServerHandler extends SimpleChannelInboundHandler<ZRpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ZRpcRequest request) {
        System.out.println(request);
    }
}
