package io.github.insorker.zrpc.test.calculator.client;

import io.github.insorker.zrpc.common.annotation.ZRpcReference;
import org.springframework.stereotype.Component;

@Component
public class MathematicsCompetition {

    @ZRpcReference("Calculator")
    private Calculator calculator;
    private final int a = 4;
    private final int b = 2;

    public void start() {
        test0();
        test1();
        test2();
        test3();
        System.out.println("Enough!");
    }

    private void printResult(boolean correct) {
        System.out.println(correct ? "YES" : "NO");
    }

    private void test0() {
        printResult(calculator.sum(a, b) == a + b);
    }

    private void test1() {
        printResult(calculator.sub(a, b) == a - b);
    }

    private void test2() {
        printResult(calculator.mul(a, b) == a * b);
    }

    private void test3() {
        printResult(calculator.div(a, b) == a / b);
    }
}
