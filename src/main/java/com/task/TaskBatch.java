package com.task;

import java.util.function.Function;

public interface TaskBatch<T> {
    //lets each call use its own r and t
    //** Method-level generics mean “this method is
    // type-polymorphic per invocation, not per instance.”

    <R extends Record> void submit(Function<R, T> method, R record) throws Exception;

    //If it were written like this:

    //public interface TaskBatch<R extends Record, T> {
    //    void submit(Function<R, T> method, R record);
    //}

    //Then every submission would have to use the same R and T.
}
