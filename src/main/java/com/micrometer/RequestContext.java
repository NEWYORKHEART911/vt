package com.micrometer;

import io.micrometer.tracing.SpanCustomizer;
import org.springframework.stereotype.Component;

@Component
public class RequestContext {

    //Micrometer Tracing stores context in the Span.
    //Custom values go into the Span tags.
    //Micrometer automatically copies Span → MDC using Slf4JEventListener.

    private final SpanCustomizer spanCustomizer;

    public RequestContext(SpanCustomizer spanCustomizer) {
        this.spanCustomizer = spanCustomizer;
    }

    public void put(String key, String value) {
        if (value != null) {
            spanCustomizer.tag(key, value);
        }
    }

    public void user(String id)      { put("userId", id); }
    public void admin(String id)     { put("adminId", id); }
    public void lob(String id)    { put("tenantId", id); }
    public void requestURI(String id)   { put("requestURI", id); }
    public void transactionID(String id)   { put("transactionID", id); }
}
