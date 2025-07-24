# Datadog APM Setup Guide

## Overview
Datadog APM has been integrated into all microservices in this e-commerce platform to provide distributed tracing and performance monitoring.

## What's Been Added

### 1. Dependencies
- Added `dd-trace-api` dependency to the parent `pom.xml` and all microservice modules
- Version: 1.38.1

### 2. Configuration
Each microservice now has:
- Datadog properties in `application.properties`:
  ```properties
  dd.service=<service-name>
  dd.env=development
  dd.version=1.0.0
  dd.trace.enabled=true
  dd.logs.injection=true
  dd.profiling.enabled=false
  ```
- A `DatadogConfig` class that initializes the tracer

### 3. Tracing Annotations
Controllers can use `@Trace` annotations for custom span creation:
```java
@GetMapping
@Trace(operationName = "product.findAll")
public Flux<List<ProductDto>> findAll() {
    // method implementation
}
```

## Running with Datadog

### 1. Set up Datadog Agent
First, copy the environment template and add your Datadog API key:
```bash
cp .env.datadog .env
# Edit .env and add your DD_API_KEY
```

### 2. Start Services with Datadog
Use the docker-compose override file to run all services with Datadog:
```bash
# Start all services with Datadog APM enabled
docker-compose -f docker-compose.yml -f docker-compose.datadog.override.yml up -d

# Or just the Datadog agent
docker-compose -f docker-compose-datadog.yml up -d
```

### 3. Run Microservices with Java Agent
When running your microservices, include the Datadog Java agent:

```bash
# Download the Datadog Java agent
wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'

# Run your service with the agent
java -javaagent:dd-java-agent.jar \
     -Ddd.agent.host=localhost \
     -Ddd.agent.port=8126 \
     -jar your-service.jar
```

### 4. Docker Deployment
For Docker deployments, add these environment variables to your service containers:
```yaml
environment:
  - DD_AGENT_HOST=datadog-agent
  - DD_TRACE_AGENT_PORT=8126
```

And add the Java agent to your Dockerfile:
```dockerfile
ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar
ENTRYPOINT ["java", "-javaagent:dd-java-agent.jar", "-jar", "app.jar"]
```

## Viewing Traces

1. Log into your Datadog account
2. Navigate to APM > Traces
3. You should see traces from all your microservices
4. Use the Service Map to visualize service dependencies

## Best Practices

1. **Use meaningful operation names** in `@Trace` annotations
2. **Add custom tags** to traces for better filtering:
   ```java
   GlobalTracer.get().activeSpan().setTag("user.id", userId);
   ```
3. **Monitor database queries** - they're automatically traced
4. **Set up alerts** for high latency or error rates

## Troubleshooting

### No traces appearing:
1. Check that the Datadog agent is running: `docker ps | grep datadog`
2. Verify connectivity: `curl http://localhost:8126/v0.3/traces`
3. Check application logs for Datadog initialization messages

### Missing service dependencies:
1. Ensure all services have unique `dd.service` names
2. Verify inter-service HTTP calls are being traced
3. Check that all services are sending traces to the same Datadog agent

## Additional Resources
- [Datadog Java APM Documentation](https://docs.datadoghq.com/tracing/setup_overview/setup/java/)
- [Datadog Trace Annotations](https://docs.datadoghq.com/tracing/trace_collection/custom_instrumentation/java/)
- [Spring Boot Integration](https://docs.datadoghq.com/integrations/spring_boot/)