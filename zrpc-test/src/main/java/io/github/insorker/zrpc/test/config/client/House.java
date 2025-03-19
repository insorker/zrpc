package io.github.insorker.zrpc.test.config.client;

import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import org.springframework.stereotype.Component;

@Component
public class House {

    @ZRpcReference("PersonService")
    private PersonService personService;

    public void whoLives() {
        System.out.println(personService.getName());
    }
}
