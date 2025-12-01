package com.utils;

public class Tl {

   //creating threads without logging prevention:
   //need postBeanProcessor for runtime vt creation prevention
   //used java sealed class

   //in library :
   //define execute, submit, executeAsync methods to handle LoggingContext transfer
   //clear ThreadLocal after vt ends execution

   //test these vt methods: execute, submit, executeAsync
   //submit(Runnable) creates and starts new virtual thread to run task- returns Future

   //https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-144A9694-40FD-4F63-97D2-8D394450F5D2
   //JDK Flight Recorder Events for VTs
   //jdk.VirtualThreadStart and jdk.VirtualThreadEnd indicuate when a vt starts and ends


   public static final ThreadLocal<LoggingContext> threadlocal = new ThreadLocal<>();

   //this wasn't necessary for some reason..
   //need to look at the class setup.. somehow this was accessible directly
   public static LoggingContext getContext() {
      return threadlocal.get();
   }

   public static void setContext(LoggingContext context) {
      threadlocal.set(context);
   }

   public static void clear() {
      threadlocal.remove();
   }

}
