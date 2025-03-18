package io.github.insorker.zrpc.client.handler;

import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ZRpcClientHandler extends SimpleChannelInboundHandler<ZRpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcClientHandler.class);
    private volatile Channel channel;
    private final ConcurrentHashMap<String, ZRpcFuture> pendingFutureMap = new ConcurrentHashMap<>();

    public ZRpcFuture sendRequest(ZRpcRequest request) {
        ZRpcFuture zRpcFuture = new ZRpcFuture();
        pendingFutureMap.put(request.getId(), zRpcFuture);

        try {
            ChannelFuture channelFuture = channel.writeAndFlush(request).sync();
            if (!channelFuture.isSuccess()) {
                logger.error("Send request {} error", request);
            }
        } catch (InterruptedException e) {
            logger.error("Send request {} error: {}", request, e);
        }

        return zRpcFuture;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ZRpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        ZRpcFuture future = pendingFutureMap.get(requestId);

        if (future != null) {
            pendingFutureMap.remove(requestId);
            future.setResponse(response);
        }
        else {
            logger.error("Cannot find pending request {} for response {}", requestId, response.getId());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }
}
