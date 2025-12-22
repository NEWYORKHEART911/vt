package com.task;

public interface TaskBatchEx<T> {

    <R extends Record> void submit(SerializableFunction<R, T> method, R record);

}