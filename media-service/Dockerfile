FROM eclipse-temurin:21-jre-alpine

# Download Datadog Java agent
ADD https://dtdg.co/latest-java-tracer /dd-java-agent.jar

COPY target/media*.jar app.jar

ENTRYPOINT ["java", "-javaagent:/dd-java-agent.jar", "-jar", "/app.jar"]
