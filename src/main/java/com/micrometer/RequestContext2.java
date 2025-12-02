package com.micrometer;

import io.micrometer.tracing.SpanCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestContext2 {

    private static final Logger log = LoggerFactory.getLogger(RequestContext.class);
    private final SpanCustomizer spanCustomizer;

    public RequestContext2(SpanCustomizer spanCustomizer) {
        this.spanCustomizer = spanCustomizer;
    }

    public void put(String key, String value) {
        if (value != null) {
            spanCustomizer.tag(key, value);
        }
    }

    // Fluent logging with key-value pairs
    public LogBuilder atLevel(Level level) {
        return new LogBuilder(level);
    }

    public class LogBuilder {
        private final Level level;
        private final Map<String, String> kvPairs = new HashMap<>();

        LogBuilder(Level level) {
            this.level = level;
        }

        public LogBuilder addKeyValue(String key, String value) {
            kvPairs.put(key, value);
            spanCustomizer.tag(key, value); // Also add to span
            return this;
        }

        public void log(String message) {
            // Use structured logging (Logback supports key-value pairs)
            LoggingEventBuilder builder;

            switch (level) {
                case TRACE: builder = log.atTrace(); break;
                case DEBUG: builder = log.atDebug(); break;
                case INFO: builder = log.atInfo(); break;
                case WARN: builder = log.atWarn(); break;
                case ERROR: builder = log.atError(); break;
                default: builder = log.atInfo();
            }

            kvPairs.forEach(builder::addKeyValue);
            builder.log(message);
        }
    }

    // Existing methods...
    public void user(String id)         { put("userId", id); }
    public void admin(String id)        { put("adminId", id); }
    public void lob(String id)          { put("tenantId", id); }
    public void requestURI(String id)   { put("requestURI", id); }
    public void transactionID(String id){ put("transactionID", id); }

    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
