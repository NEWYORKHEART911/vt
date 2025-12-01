package com.xc;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface LoggingAwareExecutor {
    void execute(Runnable task);
    <T> Future<T> submit(Callable<T> task);
    CompletableFuture<Void> executeAsync(Runnable task);
}
