package com.contextaware;

import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;

public final class ContextAwareTaskScope<T> extends StructuredTaskScope<T> {

    private final TransactionContext parentContext;

    public ContextAwareTaskScope(TransactionContext ctx) {
        this.parentContext = ctx;
    }

    @Override
    public <U extends T> Subtask<U> fork(Callable<? extends U> task) {
        TransactionContext snapshot = parentContext.copy();  //use ScopedValue -> .get() reference

        return super.fork(() -> { //have to check what depth this is
            // Restore context for this virtual thread
            ContextHolder.set(snapshot);
            try {
                return task.call();
            } finally {
                ContextHolder.clear();
            }
        });
    }
}

