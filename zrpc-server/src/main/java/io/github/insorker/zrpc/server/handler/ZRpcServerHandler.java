package io.github.insorker.zrpc.server.handler;

import io.github.insorker.zrpc.common.codec.ZRpcBeat;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;
import java.util.UUID;

public class ZRpcServerHandler extends SimpleChannelInboundHandler<ZRpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcServerHandler.class);
    private final Map<ServiceInfo, Object> serviceMap;

    public ZRpcServerHandler(Map<ServiceInfo, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ZRpcRequest request) {
        logger.info("Server receive request {}", request);

        ZRpcResponse response = new ZRpcResponse();
        response.setRequestId(request.getId());

        try {
            Object result = handle(request);
            response.setId(UUID.randomUUID().toString());
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t.toString());
            logger.error("Server handle request error: ", t);
        }

        channelHandlerContext.writeAndFlush(response).addListener(channelFuture -> {
            logger.info("Send response {} for request {}", response, request.getId());
        });
    }

    private Object handle(ZRpcRequest request) throws Throwable {
        String serviceName = request.getClassName();
        Object serviceBean = serviceMap.get(new ServiceInfo(serviceName));

        if (serviceName == null) {
            logger.error("Cannot find relative service by request {}", request);
            return null;
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterType = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass service = FastClass.create(serviceClass);
        int methodIndex = service.getIndex(methodName, parameterType);
        return service.invoke(methodIndex, serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Server caught exception: " + cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            logger.warn("Channel idle in last {} seconds, close it", ZRpcBeat.BEAT_TIMEOUT);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
