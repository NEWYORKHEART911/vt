package com.micrometer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.*;

public class RequestFilter implements Filter {

    private final RequestContext context;

    public RequestFilter(RequestContext context) {
        this.context = context;
    }

    //put this in an empty project and throw execution from here and controller on different threads
    //see how the framework handles executions and context updates across threads

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        // These can be added/updated later too
        context.put("requestPath", request.getRequestURI());
        context.put("requestId", UUID.randomUUID().toString());

        chain.doFilter(req, res);
    }

}
