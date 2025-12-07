package com.utils;

import java.util.concurrent.*;

public class LoggingContextVirtualExecutor {

    //write the outline for now - need unit tests for these new methods
    private static final LoggingContextVirtualExecutor INSTANCE = new LoggingContextVirtualExecutor();
    private final ExecutorService virtualExecutor;


    //or Better: Use ThreadPoolExecutor's afterExecute Hook
    private LoggingContextVirtualExecutor() {
        //The JDK specifies that virtual threads created by this executor get names of the form:
        //VirtualThread[#<id>]/task
        this.virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        virtualExecutor.execute(() -> {
            System.out.println("NAME = [" + Thread.currentThread().getName() + "]");
        });
    }

    public static LoggingContextVirtualExecutor getInstance() {
        return INSTANCE;
    }

    //qualify as runnable: ** lambdas ideal because it lets me do anything
//    Classes implementing Runnable
//    Anonymous classes implementing Runnable
//    Lambdas () -> { ... }
//    Method references matching void method()
//    Thread
//            FutureTask
//    ForkJoinTask (indirectly Runnable/Callable hybrids)
//    CompletableFuture internal tasks
//            TimerTask
//    Custom wrappers implementing Runnable
    public void execute(Runnable task) {
        LoggingContext context = Tl.getContext();
        System.out.println("in1 ");  //before in2
        System.out.println("before VT = [" + Thread.currentThread() + "]");
        System.out.println(virtualExecutor.getClass());

        virtualExecutor.execute(() -> {  //begin new virtual thread
            System.out.println("begin new VT = [" + Thread.currentThread().getStackTrace()[2] + "]");
            System.out.println("in3");  //before in2
            try {
                if(context != null) {
                    Tl.setContext(context);
                }  //else new - depends on condition of start context
                System.out.println("VT = [" + Thread.currentThread() + "]");
                //this should be the new virtual thread
                //since executing the vt
                task.run();  //does everything before run before running the lambda code
            } finally {
                Tl.clear();
            }
        });
    }

    public <T> Future<T> submit(Callable<T> task) {
        LoggingContext context = Tl.getContext();

        return virtualExecutor.submit(() -> {
            try {
                if(context != null) {
                    Tl.setContext(context);
                }
                return task.call();
            } finally {
                Tl.clear();
            }
        });
    }

    public CompletableFuture<Void> executeAsync(Runnable task) {
        LoggingContext context = Tl.getContext();

        return CompletableFuture.runAsync(() -> {
            try {
                if(context != null) {
                    Tl.setContext(context);
                }
                task.run();
            } finally {
                Tl.clear();
            }
        }, virtualExecutor);
    }

}
