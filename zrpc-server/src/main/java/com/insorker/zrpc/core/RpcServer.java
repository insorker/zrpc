package com.insorker.zrpc.core;

import com.insorker.zrpc.exceptions.RpcException;

/**
 * rpc 服务端
 */
public interface RpcServer {

    public void start() throws RpcException;

    public void stop() throws RpcException;
}
