package io.github.insorker.zrpc.client.proxy;

import io.github.insorker.zrpc.client.ZRpcClient;
import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ServiceProxy<T, R> implements InvocationHandler, ZRpcService<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
    private final Class<T> clazz;

    public ServiceProxy(Class<T> cls) {
        this.clazz = cls;
    }

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

    @Override
    public ZRpcFuture call(String functionName, Object... args) throws ZRpcException {
        String serviceName = clazz.getName();
        ZRpcClientHandler handler = ZRpcClient.getInstance().chooseHandler(new ServiceInfo(serviceName));
        ZRpcRequest request = new ZRpcRequest(this.clazz.getName(), functionName, args);
        return handler.sendRequest(request);
    }

    @Override
    public ZRpcFuture call(SerializableFunction<T, R> function, Object... args) throws ZRpcException {
        return call(function.getLambdaMethodName(), args);
    }
}
