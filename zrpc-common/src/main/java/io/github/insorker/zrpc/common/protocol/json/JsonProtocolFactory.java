package io.github.insorker.zrpc.common.protocol.json;

import io.github.insorker.zrpc.common.protocol.ZRpcProtocol;
import io.github.insorker.zrpc.common.protocol.ZRpcProtocolFactory;

public class JsonProtocolFactory implements ZRpcProtocolFactory {

    @Override
    public <T> ZRpcProtocol<T> createProtocol() {
        return new JsonProtocol<>();
    }
}
