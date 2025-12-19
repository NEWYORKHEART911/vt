package com.methodcall;

import java.util.List;
import java.util.function.Consumer;

public record MethodCall<R extends Record>(
        Consumer<R> method,
        List<R> records
) { }
