package io.github.insorker.zrpc.common.protocol;

public interface ZRpcProtocolFactory {

    public <T> ZRpcProtocol<T> createProtocol();
}
