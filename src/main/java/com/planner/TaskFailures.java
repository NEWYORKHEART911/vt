package com.planner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TaskFailures {

    private final List<Throwable> failures = new CopyOnWriteArrayList<>(); //terrible

    public void record(Throwable t) {
        failures.add(t);
    }

    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    public void throwIfAny() {
        if(!failures.isEmpty()) {
            RuntimeException aggregated =
                    new RuntimeException("one or more subtasks failed");
            failures.forEach(aggregated::addSuppressed);
            throw aggregated;
        }
    }

    public List<Throwable> snapshot() {
        return List.copyOf(failures);
    }

}
