package io.github.insorker.zrpc.common.exceptions;

/**
 * rpc 异常类
 */
public class ZRpcException extends RuntimeException {

    public ZRpcException(String msg) {
        super(msg);
    }
}
