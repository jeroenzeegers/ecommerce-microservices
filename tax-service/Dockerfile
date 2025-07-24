FROM eclipse-temurin:21-jre-alpine
COPY target/tax*.jar app.jar
ENTRYPOINT ["java", "-javaagent:dd-java-agent.jar", "-Ddd.profiling.enabled=false", "-XX:FlightRecorderOptions=stackdepth=256", "-jar", "/app.jar"]

