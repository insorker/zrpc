package io.github.insorker.zrpc.client.proxy;

import io.github.insorker.zrpc.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;

public interface ZRpcService<T, R> {

    ZRpcFuture call(String functionName, Object ...args) throws ZRpcException;

    ZRpcFuture call(SerializableFunction<T, R> function, Object ...args) throws ZRpcException;
}
