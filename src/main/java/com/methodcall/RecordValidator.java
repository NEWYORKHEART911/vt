package com.methodcall;

//implement in another class
public interface RecordValidator {
    <R extends Record> void validate(R record);
}
