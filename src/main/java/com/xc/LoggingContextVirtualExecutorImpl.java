package com.xc;

import com.utils.LoggingContext;
import com.utils.Tl;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestScope;

import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

@Component
public class LoggingContextVirtualExecutorImpl implements LoggingAwareExecutor {

    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    Phaser phaser = new Phaser();
    StampedLock lock = new StampedLock();  //dont use this

    @Override
    public void execute(Runnable task) {
        LoggingContext context = Tl.getContext();
        //add RequestScope
        RequestScope scope = context != null ? context.getScope() : null;

        virtualExecutor.execute(() -> {
            try {
                if (context != null) {
                    Tl.setContext(context);
                }
                task.run();
            } finally {
                Tl.clear();
            }
        });
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        LoggingContext context = Tl.getContext();

        return virtualExecutor.submit(() -> {
            try {
                if (context != null) {
                    Tl.setContext(context);
                }
                return task.call();
            } finally {
                Tl.clear();
            }
        });
    }

    @Override
    public CompletableFuture<Void> executeAsync(Runnable task) {
        LoggingContext context = Tl.getContext();

        return CompletableFuture.runAsync(() -> {
            try {
                if (context != null) {
                    Tl.setContext(context);
                }
                task.run();
            } finally {
                Tl.clear();
            }
        }, virtualExecutor);
    }

}
