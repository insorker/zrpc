package io.github.insorker.zrpc.test.service.impl;

import io.github.insorker.zrpc.common.annotation.ZRpcService;
import io.github.insorker.zrpc.test.service.AnimalService;

@ZRpcService(AnimalServiceImpl.class)
public class AnimalServiceImpl implements AnimalService {

    private String name;

    public AnimalServiceImpl() {

    }

    public AnimalServiceImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
