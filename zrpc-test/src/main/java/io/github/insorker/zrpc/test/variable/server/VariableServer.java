package io.github.insorker.zrpc.test.variable.server;

import io.github.insorker.zrpc.common.registry.ServiceInfo;
import io.github.insorker.zrpc.server.ZRpcServer;

public class VariableServer {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 7512;
        String registryAddress = "1.94.213.53:2181";

        ZRpcServer server = new ZRpcServer(host, port, registryAddress);
        server.addService(new ServiceInfo("HelloWorld"), "HelloWorld");
        server.addService(new ServiceInfo("PersonService"), new PersonServiceImpl("Harry Potter"));
        server.start();
    }
}
