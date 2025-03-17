package io.github.insorker.zrpc.test.server;

import io.github.insorker.zrpc.server.ZRpcServer;

public class ZRpcServerTest {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 7512;
        String registryAddress = "1.94.213.53:2181";

        ZRpcServer zRpcServer = new ZRpcServer(host, port, registryAddress);

        zRpcServer.addService("Hello", "Hello");
        zRpcServer.start();
    }
}
