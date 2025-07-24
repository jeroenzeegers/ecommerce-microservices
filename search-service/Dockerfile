FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache bash
COPY target/search*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["java", "-javaagent:dd-java-agent.jar", "-Ddd.profiling.enabled=false", "-XX:FlightRecorderOptions=stackdepth=256", "-jar", "/app.jar"]
