package com.planner;

public sealed interface TaskFailure permits TaskFailure.ValidationFailure, TaskFailure.ExecutionFailure {

    record ValidationFailure(Throwable cause) implements TaskFailure {}
    record ExecutionFailure(Throwable cause) implements TaskFailure {}

}
