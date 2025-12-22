package com.utils;

import com.methodcall.RecordValidatorImpl;
import com.sun.jdi.ClassType;
import com.task.TaskBatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Consumer;
import java.util.function.Function;

@RestController
@RequestMapping("/test")
public class Controller implements TaskBatch {

    RecordValidatorImpl validator = new RecordValidatorImpl();

    @PostMapping("/2")
    public String y() throws InterruptedException, Exception {
        //Consumer allows us to submit multiple entries before execution
        //so thats why Consumer is good here - use .accept

        Number.FourImpl four = new Number.FourImpl("item1");
        Number.FiveImpl five = new Number.FiveImpl("five");

        //Java needs a way to refer to a method without calling it, and ClassName::methodName
        //:: reference a method without invoking it
        //for INSTANCE method reference, use this :: method name, makes sense
        Callable<String> c = bind(this::returnFour, four);  //callable type is the return type

        //i dont think need submit and bind? it does the same thing - bind returns a callable
        //need for submission as subtask

//        submit(this::returnFour, four);

        run(ResultClass.class, batch -> {
            try {
                batch.submit(this::returnFour, four);
                batch.submit(this::returnFour, five);
                batch.submit(this::returnNull, four); //returns null with Void typing
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return c.call();
    }

//    @Override
//    public <R extends Record, T> void submit(Function<R, T> method, R record) throws Exception {
//        T result = method.apply(record);
//        System.out.println("Result= " + result);
//    }

    //must add <T> before void to add to TaskBatch - WHY DOES IT WORK LIKE THIS?!~?!
    //the method itself needs to declare the generic type parameter T so it can be
    //used in the method signature - so this is just another java thing
    //so basically putting generic in method signature like this is what DECLARES the generic
    //method level generics
    //Declared before the return type - only available to that method:
    public static <T, P> List<T> run(

            Class<P> resultClass,
            Consumer<TaskBatch<T>> taskDefinition

    ) throws Exception {

        //so no i need to return the class Type
        //need interface to map sealed interface -> class field

        P result = resultClass.getDeclaredConstructor().newInstance();

        final var subtasks = new ArrayList<StructuredTaskScope.Subtask<T>>();  //make this T

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            //**the anonymous classis created once per run() call, no per task
            //this is fine to keep the method static
        taskDefinition.accept(new TaskBatch<T>() { //so im defining a new class under hood even tho same name
            @Override //must override because generics
            public <R extends Record> void submit(Function<R, T> method, R record) {
                //validator.validate(record);  //not needed because Record passes through this
                subtasks.add(scope.fork(() -> method.apply(record)));
            }
        });

        //*** Best practice: Handle retries inside each task, before it completes.

        // Wait for all to complete (or first failure)
            scope.join();
            scope.throwIfFailed();

            //handle mapping failure - if sealed interface defined right
            // then it shouldnt even compile but make sure
            for(StructuredTaskScope.Subtask task : subtasks) {
                if(task.state().equals(StructuredTaskScope.Subtask.State.SUCCESS)) {
                    task.get(); //set to class type - needs the applyTo function
                }
            }

            return subtasks.stream().map(StructuredTaskScope.Subtask::get).toList();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    //example of retry based on certain exceptions (maybe usable concept)
    public static <T> T returnOnTransientFailure(
            Callable<T> task,
            int maxRetries
    ) throws Exception {
        Exception lastException = null;

        for(int attempt = 0; attempt <= maxRetries; attempt++ ) {
            try {
                return task.call();
            } catch(SQLException | IOException e) {
                lastException = e;
                if(attempt < maxRetries) {
                    Thread.sleep(100 * (attempt + 1));
                }
            }
        }
        throw lastException;
    }

    private String returnFour(Number number) {
        return number.item();  //just put the field name to get the value with record
    }

    private Void returnNull(Number number) {
        return null;
    }

    public <R extends Record, T> Callable<T> bind(
            Function<R, T> method,  //where R is the function argument, T is the callable
            R record
    ) {
        return () -> method.apply(record);  //callable
    }

    @Override
    public void submit(Function method, Record record) throws Exception {

    }
}
