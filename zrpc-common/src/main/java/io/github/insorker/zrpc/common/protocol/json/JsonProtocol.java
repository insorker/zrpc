package io.github.insorker.zrpc.common.protocol.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import io.github.insorker.zrpc.common.protocol.ZRpcProtocol;

public class JsonProtocol<T> extends ZRpcProtocol<T> {

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> cls) {
        return JSON.parseObject(bytes, cls, JSONReader.Feature.SupportClassForName);
    }
}
