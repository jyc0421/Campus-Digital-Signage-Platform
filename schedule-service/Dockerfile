FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/schedule-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

