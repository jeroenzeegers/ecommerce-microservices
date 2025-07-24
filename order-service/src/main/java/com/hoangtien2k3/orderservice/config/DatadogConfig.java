package com.hoangtien2k3.orderservice.config;

import datadog.trace.api.GlobalTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class DatadogConfig {

    @Value("${dd.service}")
    private String serviceName;

    @Value("${dd.env}")
    private String environment;

    @Value("${dd.version}")
    private String version;

    @PostConstruct
    public void init() {
        GlobalTracer.get();
        System.out.println("Datadog APM initialized for service: " + serviceName);
    }
}
