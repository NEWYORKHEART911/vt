package com.blocker;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

//dont need the threadpool one because virtual thread doesn't use/define a pool
//just creates a new thread as needed, but a cap can be defined

//public sealed interface LoggingAwareExecutor
//        permits LoggingContextVirtualExecutorImpl, LoggingContextThreadPoolExecutorImpl {
//
//    void execute(Runnable task);
//    <T> Future<T> submit(Callable<T> task);
//    CompletableFuture<Void> executeAsync(Runnable task);
//}

// Only these implementations are allowed
//public final class LoggingContextVirtualExecutorImpl implements LoggingAwareExecutor {
//    // Implementation with guaranteed cleanup
//}
//
//public final class LoggingContextThreadPoolExecutorImpl implements LoggingAwareExecutor {
//    // Implementation with guaranteed cleanup
//}