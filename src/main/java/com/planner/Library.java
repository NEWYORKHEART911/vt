package com.planner;

import com.methodcall.RecordValidator;
import com.methodcall.RecordValidatorImpl;

import java.util.function.Function;

public final class Library {  //exposed to public

    public PlanHandle newPlan() {
        return new PlanHandle(new ExecutionPlanner(new RecordValidatorImpl())); //cant cache like this
    }

    public static final class PlanHandle implements AutoCloseable {

        private ExecutionPlanner planner;
        private ExecutionPlan plan;

        PlanHandle(ExecutionPlanner planner) {
            this.planner = planner;
        }

        public <R extends Record, T> void submit(
                Function<R, T> method,
                R record
        ) {
            ensurePlanning();
            planner.submit(method, record);
        }

        public void execute() {
            ensurePlanning();
            plan = planner.freeze();
            planner = null;
            plan.execute();
        }

        @Override
        public void close() {
            if(planner != null) {
                throw new IllegalStateException(
                        "plan closed with execute()"
                );
            }
        }

        private void ensurePlanning() {
            if(planner == null) {
                throw new IllegalStateException(
                        "ExecutionPlan already frozen"
                );
            }
        }

    }
}
