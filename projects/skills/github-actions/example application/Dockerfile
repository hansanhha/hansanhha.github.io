FROM openjdk:21-jdk-slim
LABEL authors="hansanhha"

WORKDIR /app

COPY example-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]