package io.github.insorker.zrpc.test.config.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConfigServer {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("config-server.xml");
    }
}
