package io.github.insorker.zrpc.client.proxy;

import io.github.insorker.zrpc.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;

@FunctionalInterface
public interface ZRpcService {

    ZRpcFuture call(String functionName, Object ...args) throws ZRpcException;
}
