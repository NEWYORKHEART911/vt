package com.utils;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Controller {

    @PostMapping("/2")
    public String y() throws InterruptedException {
        System.out.println("original t name=" + Thread.currentThread().getName());
        Tl.setContext(new LoggingContext());
        LoggingContext contextInit = Tl.getContext();  //its same because of threadlocal
        contextInit.setVar1("17");
        contextInit.setVar2("23");
        LoggingContextVirtualExecutor.getInstance().execute(() -> {
            System.out.println("in2");
            System.out.println("t name=" + Thread.currentThread());  //where is my thread name?
            System.out.println(Tl.getContext().getVar1());
        });
        return "complete";
    }

//    @PostMapping("/1")
//    public String x() {
//        LoggingContext context = Tl.getContext();  //i think its the same problem
//        //logging context isn't initialized - question is how was it done
//        //probably at start transaction , still has a fallback tho
//        Tl.setContext(new LoggingContext());
//        LoggingContext contextInitialized = Tl.getContext();
//        contextInitialized.setVar1("14");
//        contextInitialized.setVar2("21");
//
//        //basically just a pooled thread
//        //executor.submit() does not immediately run task on current thread
//        //it schedules it for execution by the ExecutorService
//        executor.submit(() -> {
//            //** the most sensible solution is one where
//            //the logging dependency must be used to create the executor
//            //which automatically manages the logging aspect
//            //otherwise cannot use virtual threads
//            //** could create code that breaks vt unless used by logging dep.
//            //think thats the only way to avoid people fucking it up
//
//            //virtual threads don't use thread pools - guess b/c different type of thread
//            threadlocal.set(contextInitialized);
//            //one thing to watch for would be disappearing logging context
//            //if main thread were to finish/return
//            System.out.println("Second thread: " + Thread.currentThread().getName());
//            doSomething();
//        });
//
//        //still executes before the submit
//        System.out.println("before start thread");
//
//        return "1";
//    }
//
//    private void doSomething() {
//        System.out.println("Executing in : " + Thread.currentThread().getName());
//        LoggingContext context2 = Tl.getContext();
//        System.out.println("context2 = " + context2.getVar1());
//    }

}
