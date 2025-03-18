package io.github.insorker.client.proxy;

import io.github.insorker.client.ZRpcClient;
import io.github.insorker.client.handler.ZRpcClientHandler;
import io.github.insorker.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.registry.ServiceInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ZRpcRequest request = new ZRpcRequest();
        request.setId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(method.getParameters());

        ServiceInfo serviceInfo = new ServiceInfo(request.getClassName());
        ZRpcClientHandler handler = ZRpcClient.getInstance().chooseHandler(serviceInfo);
        ZRpcFuture future = handler.sendRequest(request);

        return future.get();
    }
}
