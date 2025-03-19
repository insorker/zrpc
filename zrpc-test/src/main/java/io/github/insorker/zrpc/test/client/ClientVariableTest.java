package io.github.insorker.zrpc.test.client;

import io.github.insorker.zrpc.client.ZRpcClient;
import io.github.insorker.zrpc.test.service.PersonService;

public class ClientVariableTest {
    public static void main(String[] args) {
        ZRpcClient.newInstance("1.94.213.53:2181");

        PersonService service = ZRpcClient.createService(PersonService.class);
        service.setName("insorker");
        System.out.println(service.getName());
    }
}
