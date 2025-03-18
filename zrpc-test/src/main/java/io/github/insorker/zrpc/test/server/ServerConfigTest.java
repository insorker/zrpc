package io.github.insorker.zrpc.test.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerConfigTest {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("server.xml");
    }
}
