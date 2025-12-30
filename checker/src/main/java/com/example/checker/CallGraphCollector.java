package com.example.checker;

import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;

import java.util.HashSet;

//compiler code

public class CallGraphCollector extends BugChecker
        implements BugChecker.MethodTreeMatcher, BugChecker.MethodInvocationTreeMatcher {

    //all  method annotated with @ParallelEntryPoint


    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        return null;
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        return null;
    }

}
