[actuator](#actuator)

[]()

[gradle configuration](#gradle-configuration)

[built-in endpoint](#built-in-endpoint)

[customize basic endpoint](#customize-basic-endpoint)


## actuator

spring boot actuator는 스프링 부트 애플리케이션에 프로덕션 환경에서의 관리 및 모니터링 기능을 손쉽게 추가할 수 있도록 도와주는 모듈이다

애플리케이션의 상태, 성능, 구성 정보 등 스프링 부트 애플리케이션의 내부 정보를 노출하는 [엔드포인트 집합](#built-in-endpoint)을 제공한다

또한 배포한 후에도 실시간 상태 모니터링, 헬스 체크, 메트릭 수집 등을 통해 시스템의 상태를 관리할 수 있도록 해준다

spring boot actuator 의존성을 추가하면 스프링 부트의 자동 구성 메커니즘에 의해 애플리케이션 내에서 제공 가능한 엔드포인트들을 자동으로 설정한다

기본적으로 `/actuator` 경로 아래에 여러 http 엔드포인트를 통해 상태 정보를 노출하며 jmx(java management extensions) 엔드포인트를 통한 상태 정보 노출도 지원한다

기본 엔드포인트 외에도 개발자가 직접 커스텀 엔드포인트를 추가하여 특정 비즈니스 로직이나 애플리케이션의 추가 정보를 노출할 수 있다


## gradle configuration

```kotlin
plugins {
    application
    id("org.springframework.boot") version("3.4.2")
    id("io.spring.dependency-management") version("1.1.7")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```


## built-in endpoint

[모든 빌트인 엔드포인트 목록](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints)

스프링 부트 액추에이터는 기본적으로 설정을 통해 노출과 접근이 허용되어야 http 또는 jvm를 통해 외부에서 엔드포인트로 접근할 수 있다 

기본 제공 엔드포인트: `/actuator` `/actuator/health` `/actuator/health/{*path}`

### `/actuator`

접근 가능한 actuator 엔드포인트 목록을 제공하는 엔드포인트

### `/actuator/health`

애플리케이션 전반적인 상태(UP, DOWN 등)를 확인할 수 있는 기본적으로 제공되는 엔드포인트

데이터베이스 연결, 메시지 브로커, 기타 종속 서비스의 상태 등을 포함시킬 수 있다

### `/actuator/info`

애플리케이션 버전, 빌드 정보, 기타 커스텀 정보를 제공하는 엔드포인트

프로퍼티 파일에 관련 정보를 추가하여 커스터마이징할 수 있다

```yaml
```

### `/actuator/metrics`

애플리케이션의 성능 지표(http 요청 수, 응답 시간, jvm 메모리 사용량 등) 제공하는 엔드포인트

외부 모니터링 도구(prometheus, grafana 등)와 연동할 수 있다

### `/actuator/env`

애플리케이션의 구성 속성 및 환경 변수 정보를 제공하는 엔드포인트

운영 환경에서 설정 값을 확인하거나 디버깅할 때 유용하다

### /actuator/loggers

애플리케이션의 로그 레벨 정보를 제공하는 엔드포인트

필요에 따라 실시간으로 변경할 수 있다

### `/actuator/threaddump`

현재 애플리케이션의 스레드 상태를 덤프하여 보여주는 엔드포인트

성능 병목이나 데드락을 분석할 때 사용된다

### `/actuator/heapdump`

jvm 힙 덤프를 생성하여 보여주는 엔트포인트

메모리 누수 등의 문제를 진단할 수 있다


## customize basic endpoint
