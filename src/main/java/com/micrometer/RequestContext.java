package com.micrometer;

import io.micrometer.tracing.SpanCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

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

    //00
    private static final Logger log = LoggerFactory.getLogger(RequestContext.class);

    // Log with specific level
    public void log(String message, Level level) {
        switch (level) {
            case TRACE:
                log.trace(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
        }
    }

    // Log with key-value pairs at specific level
    public void log(String message, Map<String, String> tags, Level level) {
        // Add tags to span
        tags.forEach(this::put);

        // Build log message with tags
        String taggedMessage = message + " " +
                tags.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", ", "[", "]"));

        log(taggedMessage, level);
    }

    // Convenience methods
    public void logInfo(String message) {
        log(message, Level.INFO);
    }

    public void logDebug(String message) {
        log(message, Level.DEBUG);
    }

    public void logWarn(String message) {
        log(message, Level.WARN);
    }

    public void logError(String message) {
        log(message, Level.ERROR);
    }

    // Level enum
    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
