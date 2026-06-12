# Use the industry-standard Eclipse Temurin Java 17 runtime
FROM eclipse-temurin:17-jre
COPY target/one-time-link-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]