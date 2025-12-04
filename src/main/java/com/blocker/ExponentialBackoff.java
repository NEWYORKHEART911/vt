package com.blocker;

import java.util.concurrent.atomic.AtomicStampedReference;
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

    public void backoff() {
        long v;
        int spins = 1;

        //AtomicStampedReference
        AtomicStampedReference<Object> atomicRef = new AtomicStampedReference<>(new Object(), 0);
        while (!atomicRef.compareAndSet(new Object(), new Object(), 0, 0)) {

            // Phase 1: spin with exponential backoff
            for (int i = 0; i < spins; i++) {
                Thread.onSpinWait();           // JDK 9+ CPU hint
            }

            //test how long it takes to reach this limit ****
            //chat GPT :
            //One iteration costs some number of CPU cycles (call it C)
//            If the CPU runs at frequency F Hz, then time per iteration ≈ C / F seconds.
//             So iterations per second ≈ F / C.
            if (spins < 512) {                 // cap spin
                spins <<= 1;                   // exponential backoff
            } else {
                // Phase 2: park minimal nanos
                LockSupport.parkNanos(1);
            }

            // Phase 3: potential fallback here
        }
    }


}
