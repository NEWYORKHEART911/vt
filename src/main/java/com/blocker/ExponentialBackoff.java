package com.blocker;

import java.util.concurrent.locks.LockSupport;

public class ExponentialBackoff {

    //This version mirrors Striped64 (LongAdder internal class):
    //this is the best

    //what it accomplishes:
//    Key points:
//    exponential spin backoff (1 → 2 → 4 → 8 → …)
//    capped at a threshold
//    escalates into minimal parking
//    avoids hogging CPU but still makes progress
//    reduces CAS collision rate
//    This produces excellent stability in high contention.

    long v;
    int spins = 1;

    //AtomicStampedReference
    while (!atomic.compareAndSet(prev, next)) {

        // Phase 1: spin with exponential backoff
        for (int i = 0; i < spins; i++) {
            Thread.onSpinWait();           // JDK 9+ CPU hint
        }

        if (spins < 512) {                 // cap spin
            spins <<= 1;                   // exponential backoff
        } else {
            // Phase 2: park minimal nanos
            LockSupport.parkNanos(1);
        }

        // Phase 3: potential fallback here
    }


}
