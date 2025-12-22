package com.task;

import com.methodcall.RecordValidator;
import com.methodcall.RecordValidatorImpl;
import com.planner.TaskFailures;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.methodcall.gen.bind;

public final class TaskRunner {

    private final RecordValidatorImpl validator;

    public TaskRunner(RecordValidatorImpl validator) {
        this.validator = validator;
    }

    public void run(Consumer<TaskBatch> definition) {

        final var failures = new TaskFailures();
        final var tasks = new ArrayList<Callable<?>>();

        // 🔒 Submission + validation phase
//        definition.accept((method, record) -> {
//            try {
//                validator.validate(record);
//                tasks.add(bind(method, record));
//            } catch (Throwable t) {
//                failures.record(t);
//            }
//        });

        // ❌ Abort before execution if validation failed
        failures.throwIfAny();

        // ▶ Execution phase (single scope)
        try (var scope =
                     new StructuredTaskScope.ShutdownOnFailure()) {

            for (Callable<?> task : tasks) {
                scope.fork(() -> {
                    try {
                        return task.call();
                    } catch (Throwable t) {
                        failures.record(t);
                        throw t;
                    }
                });
            }

            scope.join();
            scope.throwIfFailed();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // ❌ Aggregate execution failures
        failures.throwIfAny();
    }



}

