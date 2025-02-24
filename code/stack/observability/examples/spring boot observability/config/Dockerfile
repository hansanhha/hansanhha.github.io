FROM gradle:8.10-jdk21-alpine AS builder
WORKDIR /project
COPY . .
RUN gradle --no-daemon build

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /project/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]