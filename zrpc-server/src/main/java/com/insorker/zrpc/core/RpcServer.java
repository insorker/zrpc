package com.insorker.zrpc.core;

import com.insorker.zrpc.exceptions.ZRpcException;

/**
 * rpc 服务端
 */
public interface RpcServer {

    public void start() throws ZRpcException;

    public void stop() throws ZRpcException;
}
