package com.insorker.zrpc.protocol;

import com.alibaba.fastjson2.JSON;

public class JsonProtocol<T> extends ZRpcProtocol<T> {

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> cls) {
        return JSON.parseObject(bytes, cls);
    }
}
