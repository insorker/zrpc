package io.github.insorker.zrpc.client.handler;

import io.github.insorker.zrpc.common.protocol.ZRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class ZRpcFuture implements Future<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ZRpcClientHandler.class);
    private ZRpcResponse response;
    private final ReentrantLock lock;

    public ZRpcFuture() {
        this.response = null;
        this.lock = new ReentrantLock();
    }

    public void setResponse(ZRpcResponse response) {
        lock.lock();
        try {
            this.response = response;
        }
        finally {
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
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        lock.lock();
        try {
            if (response != null) {
                return response.getResult();
            }
            else {
                return null;

            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
