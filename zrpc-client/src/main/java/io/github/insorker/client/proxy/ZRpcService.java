package io.github.insorker.client.proxy;

import io.github.insorker.client.handler.ZRpcFuture;
import io.github.insorker.zrpc.common.exceptions.ZRpcException;

@FunctionalInterface
public interface ZRpcService {

    ZRpcFuture call(String functionName, Object ...args) throws ZRpcException;
}
