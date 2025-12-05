package com.xc;

import com.utils.LoggingContext;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class LoggingVirtualThreadExecutor implements ContextAwareExecutor {

    private final ExecutorService delegate =
            Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    @Override
    public void execute(Runnable task) {
        var ctx = LoggingContext.capture(); // your immutable snapshot
        delegate.execute(() -> runWithContext(ctx, task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        var ctx = LoggingContext.capture();
        return delegate.submit(() -> runWithContext(ctx, task));
    }

    private <T> T runWithContext(LoggingContext ctx, Callable<T> task) throws Exception {
        LoggingContext.install(ctx);
        try {
            return task.call();
        } catch (Throwable t) {
            LoggingContext.logErrorEvent(t);
            throw t;
        } finally {
            LoggingContext.clear();
        }
    }

    @Override
    public void close() {
        delegate.shutdown();
    }
}

