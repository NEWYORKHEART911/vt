package com.planner;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public final class ExecutionPlan {

    private final List<Callable<?>> tasks;
    private final TaskFailures failures;

    ExecutionPlan(List<Callable<?>> tasks, TaskFailures failures) {
        this.tasks = tasks;
        this.failures = failures;
    }

    public void execute() {
        failures.throwIfAny();

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (Callable<?> task : tasks) {
                scope.fork(withFailureCapture(task, failures));
            }

            try {
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            scope.throwIfFailed();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        failures.throwIfAny();
    }

    //Instead of forking the raw callable, you fork a failure-aware wrapper.
    //StructuredTaskScope needs to know the task failed

    static <T> Callable<T> withFailureCapture(
            Callable<T> task,
            TaskFailures failures
    ) {
        return () -> {
            try {
                return task.call();
            } catch (Throwable t) {
                failures.record(t);
                throw t;
            }
        };
    }

}
