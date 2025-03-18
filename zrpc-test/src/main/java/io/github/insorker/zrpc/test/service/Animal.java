package io.github.insorker.zrpc.test.service;

import io.github.insorker.zrpc.common.annotation.ZRpcService;

@ZRpcService(Animal.class)
public class Animal {

    private String name;

    public Animal() {

    }

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
