package io.github.insorker.zrpc.client.handler;

import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ZRpcFuture implements Future<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcFuture.class);
    private ZRpcResponse response = null;
    private boolean isDone = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition doneCondition = lock.newCondition();

    public void setResponse(ZRpcResponse response) {
        lock.lock();
        try {
            this.isDone = true;
            this.response = response;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        lock.lock();
        try {
            return isDone;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get() throws InterruptedException {
        lock.lock();
        try {
            while (!isDone) {
                doneCondition.await();
            }
            return response.getResult();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
        lock.lock();
        try {
            if (!isDone) {
                boolean success = doneCondition.await(timeout, unit);
                if (!success) {
                    throw new TimeoutException("Timeout waiting for result");
                }
            }
            return response;
        } finally {
            lock.unlock();
        }
    }
}
