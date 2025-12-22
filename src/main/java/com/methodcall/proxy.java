package com.methodcall;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public class proxy {

    public static <R extends Record, T> Callable<T> bind(
            Function<R, T> method,
            R record
    ) {
        return () -> method.apply(record);
    }

}
