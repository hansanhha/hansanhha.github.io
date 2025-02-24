이 예제 애플리케이션은 도커 컴포즈를 통해 스프링 부트(8080)와 프로메테우스 서버(9090)을 실행하고 비즈니스 로직 요청에 따라 측정된 메트릭을 확인하거나 observability 기능을 활용한다

#### 환경 설정

config 디렉토리의 도커 및 프로메테우스 파일 확인

#### 도커 컴포즈 실행

config 디렉토리 경로에서 아래의 명령어 실행

```shell
docker compose up -d
```

#### api 호출

[localhost:8080/api/users](http://localhost:8080/api/users) 요청

#### 액추에이터 프로메테우스 메트릭 확인

[localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)