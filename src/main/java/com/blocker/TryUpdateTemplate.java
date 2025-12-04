package com.blocker;

import com.utils.LogEvent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class TryUpdateTemplate {

    boolean tryUpdate(AtomicReference<LogEvent> ref, LogEvent updateFn) {
        LogEvent prev, next;
        int spins = 1;

        for (;;) {
            prev = ref.get();
            //next = updateFn.apply(prev);  --> this is the applicator function
            next = ref.get();

            // try fast path
            if (ref.compareAndSet(prev, next)) {
                return true;
            }

            // adaptive backoff
            if (spins < 32) {
                for (int i = 0; i < spins; i++) {
                    Thread.onSpinWait();
                }
                spins <<= 1;
                continue;
            }

            // medium backoff
            Thread.yield();  //cpu hint - need to refresh exactly how it works

            //beyond this point the strategy will involve doing anything to avoid catastrophic failure
            //need to know memory constraints/CPU constraints
            //expected load should be a few factors under what the maximum we can handle is

            // heavy backoff
            LockSupport.parkNanos(1);


            // escape hatch -> guarantee no starvation
            if (Thread.currentThread().isInterrupted()) {
                return false;
            }
        }
    }


}
