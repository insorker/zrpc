package com.insorker.zrpc.exceptions;

/**
 * rpc 异常类
 */
public class RpcException extends RuntimeException {

    public RpcException(String msg) {
        super(msg);
    }
}
