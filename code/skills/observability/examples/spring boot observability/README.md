이 예제는 도커 컴포즈를 통해 간단한 두 개의 스프링 부트 애플리케이션에 대한 observability(metrics, tracing, logging) 시스템 구축을 진행한다

### prerequisites

docker(docker-compose)

curl

### dependencies

metrics: micrometer observation + prometheus

tracing: micrometer observation + zipkin brave or opentelemetry + tempo

logging: loki

### running the application

observability stack 실행

```kotlin
docker compose up
```

client, server 애플리케이션 실행

```kotlin
./gradlew bootRun 
```

