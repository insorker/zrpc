package io.github.insorker.zrpc.server.registry;

import io.github.insorker.zrpc.common.registry.ServerInfo;

public abstract class ServerRegistry {

    public abstract void register(ServerInfo serverInfo);

    public abstract void unregister(ServerInfo serverInfo);

    public void start() { }

    public void close() { }
}
