package com.insorker.zrpc.core;

import com.insorker.zrpc.exceptions.ZRpcException;

/**
 * rpc 服务端
 */
public interface RpcServer {

    void start() throws ZRpcException;

    void stop() throws ZRpcException;
}
