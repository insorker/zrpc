package io.github.insorker.zrpc.test.calculator.server;

import io.github.insorker.zrpc.common.annotation.ZRpcService;

@ZRpcService("Calculator")
public class CalculatorImpl implements Calculator {

    @Override
    public int sum(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mul(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
