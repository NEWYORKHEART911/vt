package com.xc;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface ContextAwareExecutor extends Executor, AutoCloseable {
    <T> Future<T> submit(Callable<T> task);
    void execute(Runnable runnable);
}

