package com.utils;

import com.methodcall.RecordValidatorImpl;
import com.sun.jdi.ClassType;
import com.task.SerializableFunction;
import com.task.TaskBatch;
import com.task.TaskBatchEx;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        ResultClass results = run(ResultClass.class, batch -> {
            batch.submit(this::returnFour, four);
            batch.submit(this::returnFour, five);
            batch.submit(this::returnNull, four); //returns null with Void typing
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
//    public static <T, P> List<T> run(
    public static <P, T> P run(

            Supplier<P> factory,
            Consumer<TaskBatch<T>> taskDefinition

    ) {

        P result = factory.get();  //protection from instantiation failure

        final var subtasks = new ArrayList<StructuredTaskScope.Subtask<T>>();  //make this T

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            //**the anonymous classis created once per run() call, no per task
            //this is fine to keep the method static
        taskDefinition.accept(new TaskBatch<T>() { //so im defining a new class under hood even tho same name
           //same batch instance re-uses submit
            @Override //must override because generics
            public <R extends Record> void submit(Function<R, T> method, R record) {
                //validator.validate(record);  //not needed because Record passes through this

                //String methodName = extractMethodName(method);
                //with TaskBatchEx, supposedly this would work but its too complex
                //will have to find another way

                subtasks.add(scope.fork(() -> {
                    //vt doesn't use getName(), pt does
                    System.out.println("thread: " + Thread.currentThread()
                            //might need to stream stack trace - stack walker
                            //how far back should the method be?  2?
                        + " running task ");
                    return method.apply(record);
                }));
            }
        });

        //*** Best practice: Handle retries inside each task, before it completes.
            //could try a run with retries AND/OR just return <T> List<T> if voids
            //if returning void could just run @Async - dont think there is a need for that

        // Wait for all to complete (or first failure)
            scope.join();
            scope.throwIfFailed();  //this is just going to propagate but
            //it will be wrapped by ExecutionException - i want log and re-throw

            //handle mapping failure - if sealed interface defined right
            // then it shouldnt even compile but make sure
            for(StructuredTaskScope.Subtask task : subtasks) {
                if(task.state().equals(StructuredTaskScope.Subtask.State.SUCCESS)) {
                    task.get(); //set to class type - needs the applyTo function
                }
            }

            //return subtasks.stream().map(StructuredTaskScope.Subtask::get).toList();
            return result;

            //need to think about the difference between handling this at the join point
            //vs doing this at the try boundary

            //this is correct because
            //join throws ExecutionException
            //which PERSERVES THE ORIGINAL CAUSE with getCause()
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        //  } catch (ExecutionException e) {
        //            throw map(e.getCause());
        //        } catch (InterruptedException e) {
        //            Thread.currentThread().interrupt();
        //            throw new RuntimeException(e);
        //        }
        //    }
        //
        //    private RuntimeException map(Throwable t) {
        //        // central exception policy
        //        if (t instanceof RuntimeException re) {
        //            return re;
        //        }
        //        return new RuntimeException(t);
        //    }

    }

    // Extract method name
    private static String extractMethodName(SerializableFunction<?, ?> function) {
        try {
            Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(function);
            return lambda.getImplClass() + "::" + lambda.getImplMethodName();
        } catch (Exception e) {
            return function.toString();
        }
    }


    //example of retry based on certain exceptions (maybe usable concept)
    //run inside fork for example
    //scope.fork(() -> { returnOnTransientFailure() }
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
    public void submit(Function method, Record record)  {

    }
}
