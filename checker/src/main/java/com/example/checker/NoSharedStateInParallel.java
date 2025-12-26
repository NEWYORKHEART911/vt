package com.example.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.VariableTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;

@AutoService(BugChecker.class)
@BugPattern(
        name = "NoSharedStateInParallel",
        summary = "Shared mutable state is forbidden in parallel execution",
        severity = SeverityLevel.ERROR
)
public class NoSharedStateInParallel extends BugChecker
        implements VariableTreeMatcher, MethodInvocationTreeMatcher {

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        return Description.NO_MATCH;
    }

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        Symbol.VarSymbol sym = ASTHelpers.getSymbol(tree);

        if (sym != null && sym.isStatic() && !sym.isFinal()) {
            return buildDescription(tree)
                    .setMessage("Static mutable state is forbidden in parallel code")
                    .build();
        }

        return Description.NO_MATCH;
    }
}