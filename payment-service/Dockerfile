FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/payment-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
