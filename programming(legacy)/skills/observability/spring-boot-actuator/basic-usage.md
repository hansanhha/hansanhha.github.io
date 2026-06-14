---
layout: default
title:
---

[actuator](#actuator)

[gradle configuration](#gradle-configuration)

[managing endpoints](#managing-endpoints)

[spring boot actuator + spring security](#spring-boot-actuator--spring-security)

[sanitize sensitive values](#sanitize-sensitive-values)

[cors support](#cors-support)

[built-in endpoints](#built-in-endpoints)

[customize endpoint](#customize-endpoint)


## actuator

spring boot actuator는 스프링 부트 애플리케이션에 프로덕션 환경에서의 관리 및 모니터링 기능을 손쉽게 추가할 수 있도록 도와주는 모듈이다

애플리케이션의 상태, 성능, 구성 정보 등 스프링 부트 애플리케이션의 내부 정보를 노출하는 [엔드포인트 집합](#managing-endpoints)을 제공한다

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


## managing endpoints

### controlling access to endpoints

opt-in: 기본적으로 접근을 허용하지 않는 방식 -> 필요한 엔드포인트만 선택적으로 허용

opt-out: 기본적으로 접근을 허용하는 방식 -> 필요없는 엔드포인트만 선택적으로 차단 

스프링 부트 액추에이터는 활성화된 엔드포인트에 대해 기본적으로 접근을 허용한다 (opt-out, unrestricted)

기본 동작을 opt-in 방식으로 변경하려면 아래와 같이 설정하여 엔드포인트가 활성화돼도 접근이 허용되지 않게 설정할 수 있다

```yaml
management.endpoints.access.default=none
```

엔드포인트에 대한 개별 접근 허용은 `management.endpoint.<id>.access` 속성을 사용하여 설정할 수 있다

이 속성에 대한 접근 권한 범위를 설정할 수 있는 값은 다음과 같다
- read-only: 읽기 전용(get 요청만 허용)
- write: 모든 http 메서드 허용
- none: 모든 접근 차단

아래의 설정은 loggers 엔드포인트에 대해 읽기 전용 접근으로만 허용하여 `/actuator/loggers`에 get 요청은 가능하지만 post 요청(로그 레벨 변경)은 차단한다 

```yaml
management.endpoint.loggers.access=read-only
```

### exposing endpoints

엔드포인트는 `management.endpoints.<web or jmx>.exposure` 속성의 include/exclude 키워드를 통해 드러내거나 감출 수 있다

키워드에 나열된 값들은 엔드포인트의 id로 특정 엔드포인트를 노출하려면 include 키워드에 나열하고, 감추려면 exclude 키워드에 명시하면 된다

만약 exclude 키워드는 include 키워드보다 우선순위를 가지므로 두 키워드에 동시에 선언된 엔드포인트 id는 감춰진다

```yaml
management:
  endPoints:
    web:
      exposure:
        include: health, info
```

만약 애플리케이션이 방화벽 뒤에 애플리케이션을 배포하여 모든 액추에이터 엔드포인트에 대해 접근하는 것을 선호한다면 아래와 같이 `*`을 지정하여 모든 접근을 허용한다

```yaml
management:
  endPoints:
    web:
      exposure:
        include: *
```


## spring boot actuator + spring security

spring security가 클래스패스에 존재할 때 SecurityFilterChain 빈의 등록 여부에 따라 스프링 부트 액추에이터는 다음과 같이 동작한다

SecurityFilterChain 빈 등록하지 않을 시: `/actuator/health`를 제외한 모든 나머지 엔드포인트는 스프링 부트 자동 구성에 의해 보호된다

SecurityFilterChain 빈 등록 시: 엔드포인트 보호에 대한 스프링 부트의 자동 구성이 수행되지 않고 사용자에게 액추에이터 접근 규칙에 대한 모든 제어를 위임한다  

일반적으로 SecurityFilterChain 빈을 등록하기 마련이므로 액추에이터의 접근에 대한 보안 설정을 해야한다

스프링 부트는 서블릿 환경의 액추에이터 관련 설정을 할 수 있도록 `EndpointRequest`라는 클래스를 지원한다

아래의 설정은 `EndpointRequest.toAnyEndpoint()`을 통해 모든 요청을 매치하고 ENDPOINT_ADMIN role을 가진 요청만 액추에이터의 접근을 허용한다

참고로 EndpointRequest를 통한 인증/인가 설정은 액추에이터에만 적용된다

```java
@Configuration(
        proxyBeanMethods = false
)
public class ActuatorSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(requests -> requests.anyRequest().hasRole("ENDPOINT_ADMIN"))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

### csrf (cross site request forgery) protection

스프링 시큐리티의 기본 설정에 의해 csrf 설정이 활성화되면 스프링 부트 액추에이터 중 post (shutdown, loggers 등), put, delete 요청을 수행할 수 있는 엔드포인트는 403 forbidden 에러를 반환한다

스프링은 애플리케이션이 브라우저가 아닌 클라이언트에서 사용되는 경우에만 csrf 보호 설정을 비활성화하는 것을 권장한다


## sanitize sensitive values

액추에이터는 민감한 정보를 포함하고 있는 `/actuator/env` `/actuator/configprops` `/actuator/quartz` 엔드포인트가 반환하는 값을 기본적으로 `******` 으로 마스킹한다

마스킹처리된 부분의 실제 값을 보려면 `show-values` 속성의 값을 never(기본 값)가 아닌 always 또는 when-authorized으로 변경하면 된다

always 속성은 누가 접근하든 마스킹처리를 하지 않고 실제 값을 반환한다

when-authorized 속성은 인증된 사용자에게만 보여주며 인증된 사용자 중 역할 범위까지 제한할 수 있다

아래의 설정은 인증된 사용자 중 admin 역할을 가진 사용자에게만 실제 값을 보여주고 그 이외의 경우 `******`으로 마스킹 처리된 값을 반환한다

```yaml
management:
  endPoints:

  endpoint:
    env:
      show-values: when_authorized
      roles: admin
```


## cors support

스프링 부트 액추에이터는 기본적으로 cors 지원을 비활성화하고 `management.endpoints.web.cors.allowed-origins` 속성을 설정하는 경우에만 cors가 활성화된다

아래의 설정은 example.com 도메인에서 요청한 get, post 메서드에 대해서만 액추에이터 엔드포인트에 대한 접근을 허용한다

```yaml
management:
  endPoints:
    web:
      cors:
        allowed-origins: example.com
        allowed-methods: GET, POST
```


## built-in endpoints

[모든 빌트인 엔드포인트 목록](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints)

스프링 부트에서 자동적으로 구성하는 엔드포인트들 중 기본적으로 노출되는 엔드포인트를 제외한 나머지는 자동 구성되지 않고 감춰지며 각각 http 또는 jmx 접근 제어를 통해 외부에 노출할 수 있다

따라서 접근 가능한 내장된 엔드포인트만이 자동 구성의 대상이 된다

기본 공개 엔드포인트: `/actuator` `/actuator/health` `/actuator/health/{*path}`

#### `/actuator`

접근 가능한 actuator 엔드포인트 목록을 제공하는 엔드포인트

#### `/actuator/health`

애플리케이션 전반적인 상태(UP, DOWN 등)를 확인할 수 있는 기본적으로 제공되는 엔드포인트

데이터베이스 연결, 메시지 브로커, 기타 종속 서비스의 상태 등을 포함시킬 수 있다

#### `/actuator/info`

애플리케이션 버전, 빌드 정보, 기타 커스텀 정보를 제공하는 엔드포인트

프로퍼티 파일에 관련 정보를 추가하여 커스터마이징할 수 있다

```yaml
```

#### `/actuator/metrics`

애플리케이션의 성능 지표(http 요청 수, 응답 시간, jvm 메모리 사용량 등) 제공하는 엔드포인트

외부 모니터링 도구(prometheus, grafana 등)와 연동할 수 있다

#### `/actuator/env`

애플리케이션의 구성 속성 및 환경 변수 정보를 제공하는 엔드포인트

운영 환경에서 설정 값을 확인하거나 디버깅할 때 유용하다

#### `/actuator/loggers`

애플리케이션의 로그 레벨 정보를 제공하는 엔드포인트

필요에 따라 실시간으로 변경할 수 있다

#### `/actuator/threaddump`

현재 애플리케이션의 스레드 상태를 덤프하여 보여주는 엔드포인트

성능 병목이나 데드락을 분석할 때 사용된다

#### `/actuator/heapdump`

jvm 힙 덤프를 생성하여 보여주는 엔트포인트

메모리 누수 등의 문제를 진단할 수 있다


## customize endpoint

@Endpoint(jmx, http) 또는 @WebEndpoint(http only) 클래스를 빈으로 등록하여 커스텀 엔드포인트를 만들 수 있다

엔드포인트 클래스는 해당 엔드포인트에 대한 id를 지정하고 멤버 메서드에 @ReadOperation, @WriteOperation, @DeleteOperation을 적용하여 엔드포인트들을 생성할 수 있다

#### @ReadOperation

엔드포인트에 대한 get 요청으로 호출할 수 있는 메서드에 선언하는 어노테이션

메서드는 쿼리 파라미터 또는 @Selector를 선언하여 경로 변수로 값을 주입받을 수 있다

#### @WriteOperation

엔드포인트에 대한 post 요청으로 호출할 수 있는 메서드에 선언하는 어노테이션

쿼리 파라미터, 경로 변수(@Selector) 또는 json 요청 본문을 통해 값을 주입받을 수 있다

#### @DeleteOperation

엔드포인트에 대한 delete 요청으로 호출할 수 있는 메서드에 선언하는 어노테이션 

@ReadOperation과 동일한 방식으로 값을 주입받을 수 있다

#### @Selector

메서드의 파라미터는 기본적으로 쿼리 파라미터 또는 json 바디(post 요청)로만 데이터를 받을 수 있는데, @Selector를 파라미터에 적용하면 해당 파라미터를 uri 경로 변수처럼 사용할 수 있다

여러 개의 파라미터에 적용하는 경우 선언된 순서에 따라 경로 변수의 순서가 정해진다

아래의 SimpleEndpoint 클래스는 커스텀 엔드포인트 id를 simple이라고 지정하고 get 요청에 대한 메서드를 정의했는데, 이 때 매개변수에 @Selector를 선언하면 `/actuator/simple/{id}` 형식으로 엔드포인트를 노출하게 된다  

```java
@WebEndpoint(id = "simple")
public static class SimpleEndpoint {
    
    @ReadOperation
    public String usingSelector(@Selector String id) {
        return "hello simple endpoint, id: " + id;
    }
}
```

참고로 엔드포인트 클래스는 중복된 경로에 동일한 operation 어노테이션을 가진 메서드를 가질 수 없다

만약 동일한 http 메서드에 대한 요청을 여러 개 처리해야 된다면 메서드에 @Selector를 적용하여 각 메서드의 경로를 구분시켜줘야 한다  

아래의 SimpleEndpoint 클래스는 두 개의 @ReadOperation 메서드를 선언한 뒤 하나의 메서드에 @Selector를 적용시켜서 `/actuator/simple`과 `/actuator/simple/{id}`로 경로를 구분시킨다

그 밑의 OrderInfoEndpoint 클래스는 주문 정보에 대한 get, post, delete 요청을 모두 처리하는 커스텀 엔드포인트를 설정한다

```java
@Configuration
public class EndpointConfig {

    @Bean
    public OrderInfoEndpoint orderInfoEndpoint() {
        return new OrderInfoEndpoint();
    }

    @Bean
    public SimpleEndpoint simpleEndpoint() {
        return new SimpleEndpoint();
    }

    @WebEndpoint(id = "simple")
    public static class SimpleEndpoint {

        @ReadOperation
        public String get() {
            return "hello simple endpoint";
        }

        // 중복된 @XXXOperation 메서드를 선언할 수 없다
        // @Selector를 사용하여 경로를 구분하면 여러 개를 선언할 수 있다
//        @ReadOperation
//        public String duplicatedReadOperation() {
//            return "duplicated @ReadOperation";
//        }

        @ReadOperation
        public String usingSelector(@Selector String id) {
            return "hello simple endpoint, id: " + id;
        }
    }

    // 주문 정보에 대한 커스텀 엔드포인트 설정
    @WebEndpoint(id = "shallowOrder-info")
    public static class OrderInfoEndpoint {

        private final Map<Long, Order> orders = new HashMap<>();

        @ReadOperation
        public Order get(@Selector Long id) {
            return orders.get(id);
        }

        @WriteOperation
        public void save(@Selector Long id) {
            orders.put(id, Order.create(id));
        }

        @DeleteOperation
        public void delete(@Selector Long id) {
            orders.remove(id);
        }
    }

    public record Order(
            String name,
            int amount) {

        private static Order create(Long id) {
            return new Order(UUID.randomUUID().toString(), (int) (id * 10_000));
        }

    }

}
```
