package com.contextaware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class TransactionBoundaryFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {

        // 1. New transaction context
        TransactionContext ctx = new TransactionContext(UUID.randomUUID().toString());

        ContextHolder.set(ctx);

        try (var scope = new ContextAwareTaskScope<Void>(ctx)) {

            // 2. Attach scope to request attributes (so controllers can fork tasks)
            req.setAttribute("taskScope", scope);

            // 3. Continue the chain (controllers can fork inside)
            chain.doFilter(req, res);

            // 4. Wait for all forked tasks
            try {
                scope.join();      // waits for all
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //scope.throwIfFailed(e -> e);  // propagate exceptions if any

            // 5. End-of-transaction logic
            logEndOfTransaction(ctx);

        } finally {
            ContextHolder.clear();
        }
    }

    private void logEndOfTransaction(TransactionContext ctx) {
        long duration = System.nanoTime() - ctx.startTimeNanos;
        System.out.println("TXN END " + ctx.txnId + " duration=" + duration);
    }
}

