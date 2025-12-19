package com.methodcall;

import java.util.function.Function;
import java.util.function.Supplier;

public class proxy {

    //example boundary function
    //scoped tasks accept submit output

    //expose this
    //supply result as tasks

    public static <R extends Record, T> void submit(
            Forker forker,  //my forker definition
            Function<R, T> method,
            R record
    ) {
        // library owns validation & binding
        Supplier<T> task = bind(method, record);
        forker.fork(task);
    }

    private static <R extends Record, T> Supplier<T> bind(
            Function<R, T> method,
            R record
    ) {
        // generics guarantee record-ness
        return () -> method.apply(record);
    }

}
