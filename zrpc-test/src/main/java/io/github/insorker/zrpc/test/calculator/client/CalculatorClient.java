package io.github.insorker.zrpc.test.calculator.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CalculatorClient {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("calculator-client.xml");
        MathematicsCompetition competition = context.getBean(MathematicsCompetition.class);
        competition.start();
    }
}
