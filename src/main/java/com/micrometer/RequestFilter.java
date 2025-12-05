package com.micrometer;

import com.utils.Tl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import jakarta.servlet.*;
import org.springframework.web.context.request.RequestScope;

public class RequestFilter implements Filter {

    private final RequestContext context;  // so instead of using some round about thing provided by micrometer
    //directly provide SCOPE to LoggingContext @ or near start of logging transaction
    //this should definitely be done universally early in filters

    private final Duration joinTimeout = Duration.ofSeconds(30); //need timeout in case of db/external calls hanging

    public RequestFilter(RequestContext context) {
        this.context = context;
    }

    //put this in an empty project and throw execution from here and controller on different threads
    //see how the framework handles executions and context updates across threads

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        RequestScope scope = new RequestScope();
        //add scope to logging context holder ( threadlocal + scoped )

        // These can be added/updated later too
        context.put("requestPath", request.getRequestURI());
        context.put("requestId", UUID.randomUUID().toString());

        try {
            chain.doFilter(req, res);
        } finally {
            try {
                // in finally wait for all child tasks for timeout period
                if (logging context holder request scope !=null){  //should never be the case
                    List<Throwable> errors = loggingcontextholder.getRequestScope().awaitCompletionOrCancel(joinTimeout);
                    if (!errors.isEmpty()) {
                        //log errors - again @ExceptionHandler will mix
                        logging.
                    }
                }
            } finally {
                Tl.clear()
            }
        }
    }

}
