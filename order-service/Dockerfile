## start with a base image
FROM maven:latest
VOLUME /tmp
ARG PROJECT_VERSION=0.0.1
RUN mkdir -p /home/app
WORKDIR /home/app

# Download Datadog Java agent
ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar
ENV SPRING_PROFILES_ACTIVE application
COPY ./ .
ADD target/order-service-0.0.1-SNAPSHOT.jar order-service-0.0.1-SNAPSHOT.jar
EXPOSE 8888
ENTRYPOINT ["java", "-javaagent:dd-java-agent.jar", "-Ddd.profiling.enabled=false", "-XX:FlightRecorderOptions=stackdepth=256", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]
