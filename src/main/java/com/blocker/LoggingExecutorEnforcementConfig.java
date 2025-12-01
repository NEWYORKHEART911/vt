package com.blocker;

import com.xc.LoggingAwareExecutor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class LoggingExecutorEnforcementConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Prevent registration of ExecutorService beans that aren't logging-aware
        if (bean instanceof ExecutorService && !(bean instanceof LoggingAwareExecutor)) {
            throw new BeanCreationException(
                    "ExecutorService beans must implement LoggingAwareExecutor. " +
                            "Found: " + bean.getClass().getName() + " for bean: " + beanName
            );
        }
        return bean;
    }
}
