package com.methodcall;

import java.util.concurrent.Callable;

public interface Forker {

    <T> void fork(Callable<T> task);

}
