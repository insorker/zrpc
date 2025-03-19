package io.github.insorker.zrpc.test.config.server;

import io.github.insorker.zrpc.common.annotation.ZRpcService;

@ZRpcService("PersonService")
public class PersonServiceImpl implements PersonService {

    private String name = "nobody";

    public PersonServiceImpl() {

    }

    public PersonServiceImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
