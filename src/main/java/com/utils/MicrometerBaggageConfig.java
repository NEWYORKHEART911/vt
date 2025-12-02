package com.utils;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageEntryMetadata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerBaggageConfig {

    //How to Store Custom Context in Micrometer
    //SUPPOSELY (but needs to be tested):
    //propagates across virtual threads
    //flows into tracing spans
    //is inserted into MDC automatically by Micrometer

//    Micrometer Tracing automatically pushes OTel baggage → MDC.

    public void setContext(String userId, String tenantId, String adminId) {

        Baggage.current()
                .toBuilder()
                .put("userId", userId, BaggageEntryMetadata.empty())
                .put("tenantId", tenantId, BaggageEntryMetadata.empty())
                .put("adminId", adminId, BaggageEntryMetadata.empty())
                .build()
                .makeCurrent();
    }

}
