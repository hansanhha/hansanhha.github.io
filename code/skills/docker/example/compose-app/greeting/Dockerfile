# 사전에 bootJar 빌드 필요

FROM openjdk:21-jdk-slim
LABEL authors="hansanhha"

WORKDIR /app
COPY ./build/libs/compose-greeting-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]