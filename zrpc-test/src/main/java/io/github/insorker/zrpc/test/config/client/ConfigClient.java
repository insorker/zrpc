package io.github.insorker.zrpc.test.config.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConfigClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config-client.xml");
        House house = context.getBean(House.class);
        house.whoLives();
    }
}
