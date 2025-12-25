package com.ast;

import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;

public class CallGraphCollector extends BugChecker
        implements BugChecker.MethodTreeMatcher, BugChecker.MethodInvocationTreeMatcher {
    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        return null;
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        return null;
    }

}
