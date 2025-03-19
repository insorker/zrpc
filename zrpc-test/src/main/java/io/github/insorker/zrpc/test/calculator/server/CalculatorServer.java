package io.github.insorker.zrpc.test.calculator.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CalculatorServer {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("calculator-server.xml");
    }
}
