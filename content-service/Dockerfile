 FROM openjdk:17-jdk-slim
 ARG JAR_FILE=target/content-service*.jar
 COPY ${JAR_FILE} app.jar
 ENTRYPOINT ["java", "-jar", "/app.jar"]
