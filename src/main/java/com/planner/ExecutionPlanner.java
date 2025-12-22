package com.planner;

import com.methodcall.RecordValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.methodcall.gen.bind;

final class ExecutionPlanner {

    private final RecordValidator validator;
    private final TaskFailures failures = new TaskFailures();
    private final List<Callable<?>> tasks = new ArrayList<>();

    ExecutionPlanner(RecordValidator validator) {
        this.validator = validator;
    }

    <R extends Record, T> void submit(
            Function<R, T> method,
            R record
    ) {
        try {
            validator.validate(record);
            tasks.add(bind(method, record));
        } catch( Throwable t) {
            failures.record(t);
        }
    }

    ExecutionPlan freeze() {
        return new ExecutionPlan(
                List.copyOf(tasks),
                failures
        );
    }

}
