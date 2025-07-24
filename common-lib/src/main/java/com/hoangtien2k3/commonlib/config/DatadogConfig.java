package com.hoangtien2k3.commonlib.config;

import datadog.trace.api.GlobalTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DatadogConfig {

    @Value("${dd.service:common-lib}")
    private String serviceName;

    @PostConstruct
    public void init() {
        GlobalTracer.get();
        System.out.println("Datadog APM initialized for service: " + serviceName);
    }
}