package io.github.insorker.zrpc.test.variable.server;

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
