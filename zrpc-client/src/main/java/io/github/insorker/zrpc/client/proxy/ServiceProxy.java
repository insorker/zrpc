package io.github.insorker.zrpc.client.proxy;

import io.github.insorker.zrpc.client.SingletonDiscoveryClient;
import io.github.insorker.zrpc.client.handler.ZRpcClientHandler;
import io.github.insorker.zrpc.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;
import io.github.insorker.zrpc.common.protocol.ZRpcRequest;
import io.github.insorker.zrpc.common.registry.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy<T, R> implements InvocationHandler, ZRpcService<T, R> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
    private final ServiceInfo serviceInfo;
    private final Class<T> clazz;

    public ServiceProxy(ServiceInfo serviceInfo, Class<T> cls) {
        this.serviceInfo = serviceInfo;
        this.clazz = cls;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        ZRpcRequest request = new ZRpcRequest();
        request.setServiceInfo(serviceInfo);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        try {
            ZRpcClientHandler handler = SingletonDiscoveryClient.getInstance().chooseHandler(serviceInfo);
            ZRpcFuture future = handler.sendRequest(request);
            return future.get();
        } catch (Exception e) {
            logger.error("Proxy invoke error: ", e);
            return null;
        }
    }

    @Override
    public ZRpcFuture call(String functionName, Object... args) throws ZRpcException {
        String serviceName = clazz.getName();
        ZRpcClientHandler handler = SingletonDiscoveryClient.getInstance().chooseHandler(new ServiceInfo(serviceName));
        ZRpcRequest request = new ZRpcRequest(serviceInfo, clazz.getName(), functionName, args);
        return handler.sendRequest(request);
    }

    @Override
    public ZRpcFuture call(SerializableFunction<T, R> function, Object... args) throws ZRpcException {
        return call(function.getLambdaMethodName(), args);
    }
}
