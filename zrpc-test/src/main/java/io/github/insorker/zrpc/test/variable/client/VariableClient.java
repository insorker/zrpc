package io.github.insorker.zrpc.test.variable.client;

import io.github.insorker.zrpc.client.SingletonDiscoveryClient;
import io.github.insorker.zrpc.client.ZRpcClient;
import io.github.insorker.zrpc.common.registry.ServiceInfo;

public class VariableClient {
    public static void main(String[] args) {
        ZRpcClient zRpcClient = SingletonDiscoveryClient.newInstance("1.94.213.53:2181");

        PersonService service = zRpcClient.createService(new ServiceInfo("PersonService"), PersonService.class);
        System.out.println(service.getName());
        service.setName("Hermione");
        System.out.println(service.getName());
        service.setName("Ron");
        System.out.println(service.getName());
    }
}
