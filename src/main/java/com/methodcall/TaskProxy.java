package com.methodcall;

import java.util.function.Function;

public interface TaskProxy {

    //implementation performs validation and binding before submission to Scope
    <R extends Record, T> void submit(
        Function<R, T> method,
        R record
    );

}
