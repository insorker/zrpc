package io.github.insorker.zrpc.client.discovery;

import io.github.insorker.zrpc.common.registry.ServerInfo;

import java.util.List;

public abstract class Discovery {

    public abstract List<ServerInfo> discover() throws Exception;

    public void start() { }

    public void close() { }
}
