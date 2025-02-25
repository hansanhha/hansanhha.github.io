이 예제 애플리케이션은 도커 컴포즈를 통해 스프링 부트(8080)와 프로메테우스 서버(9090)을 실행하고 비즈니스 로직 요청에 따라 측정된 메트릭을 확인하거나 observability 기능을 활용한다

필요 도구: docker, curl

#### 1. 설정 파일 확인

config 디렉토리의 도커 및 프로메테우스 파일 확인

#### 2. 도커 컴포즈 실행

config 디렉토리 경로에서 아래의 명령어 실행

```shell
docker compose up -d
```

#### 3. api 호출

```shell
curl localhost:8080/users/1
```

```shell
curl -X POST localhost:8080/users
```

#### 4. 메트릭 확인

스프링 부트 액추에이터 프로메테우스 엔드포인트: [localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

프로메테우스 웹 ui: [localhost:9090](http://localhost:9090)

#### 5. 스프링 부트 액추에이터 + 프로메테우스 문서 참고

[reference](../../spring-boot-actuator/spring%20boot%20metrics.md)