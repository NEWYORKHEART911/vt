package com.methodcall;

import org.springframework.core.type.MethodMetadata;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class enforcement {

    //cache once?

    static void validate(Method method, Object input) {
        if(!(input instanceof Record)) {
            //throw not a record
        }

        Class<?> paramType = method.getParameterTypes()[0];  //why 0? assumes 1  input
        if(!paramType.isInstance(input)) {
            //throw
        }
    }

    private static final ConcurrentHashMap<Method, MethodMetadata> CACHE =
            new ConcurrentHashMap<>();

    static MethodMetadata resolve(Method method) {
        return CACHE.computeIfAbsent(method, m -> {
            Class<?>[] params = m.getParameterTypes();

            if (params.length != 1) {
                throw new IllegalArgumentException(
                        "Method must have exactly one parameter"
                );
            }

            Class<?> param = params[0];

            if (!Record.class.isAssignableFrom(param)) {
                throw new IllegalArgumentException(
                        "Method parameter must be a record, got " + param
                );
            }

            @SuppressWarnings("unchecked")
            Class<? extends Record> recordType =
                    (Class<? extends Record>) param;

            return new MethodMetadata(recordType);
        });
    }



}
