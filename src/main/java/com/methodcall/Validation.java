package com.methodcall;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class Validation implements TaskProxy {

    private final Forker forker;
    private final RecordValidator recordValidator;

    public Validation(Forker forker, RecordValidator recordValidator) {
        this.forker = forker;
        this.recordValidator = recordValidator;
    }

    //1- validation phase
    //2- execution phase

    @Override
    public <R extends Record, T> void submit(
            Function<R, T> method,
            R record
    ) {

        recordValidator.validate(record);  //use caching

        Callable<T> task = bind(method, record); //method sig uses that record type

        //this is the task to be submitted as subtask but it needs to be variable
        forker.fork(task);

    }

    public static <R extends Record, T> Callable<T> bind(
            Function<R, T> method,
            R record
    ) {
        return () -> method.apply(record);
    }

}
