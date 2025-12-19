package com.methodcall;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class gen {

    @SafeVarargs
    public static <R extends Record> void runChecks(
            Consumer<R> method,
            R... records
    ) {
        for (R record : records) {
            method.accept(record);
        }
    }

    //because StrcuturedTaskScope.fork() wants callable:
    //<T> Subtask<T> fork(Callable<T> task)

    //definitely the best option but where to apply it.
    //has to be in the library i think - pass binding to supplier
    public static <R extends Record, T> Callable<T> bind(
            Function<R, T> method,  //where R is the function argument, T is the callable
            R record
    ) {
        return () -> method.apply(record);  //callable
    }

    public static Callable<?> t() {
        return () -> "bind";
    }

}
