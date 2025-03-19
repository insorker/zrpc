package io.github.insorker.zrpc.test.service.impl;

import io.github.insorker.zrpc.test.service.PersonService;

public class PersonServiceImpl implements PersonService {

    private String name;

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
