FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/content-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

