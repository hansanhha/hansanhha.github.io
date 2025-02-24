#### index
- [metrics configuration](#metrics-configuration)
- [actuator, prometheus configuration](#actuator-prometheus-configuration)
- [metrics measurement](#metrics-measurement)
- [example project](https://www.github.com/hansanhha/hansanhha.github.io/tree/default/code/stack/observability/examples/spring%20boot%20observability)


## metrics configuration

스프링 부트 액추에이터는 메트릭 및 추적을 통합한 micrometer-observation 의존성을 포함하고 있어서 사용할 모니터링 시스템(micrometer registry implementation)에 대한 의존성만 추가해주면 된다

이 예제에서는 [프로메테우스](../prometheus/overview.md)를 사용한다 [지원되는 모니터링 시스템 목록](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export)

메트릭을 측정하는 방법은 프로그래밍 방식과 선언 방식(어노테이션 기반)으로 나뉘는데, 어노테이션 방식을 사용한다면 spring boot aop 의존성도 함께 추가해준다

```kotlin
implementation("org.springframework.boot:spring-boot-starter-aop")
implementation("org.springframework.boot:spring-boot-starter-actuator")
implementation("io.micrometer:micrometer-registry-prometheus")
```

스프링 부트 액추에이터는 클래스 패스에 micrometer 구현체(micrometer-registry-prometheus)가 존재하는 경우 CompositeMeterRegistry에 해당 registry에 각 구현체를 삽입한다

또한 ObservationRegistry 타입의 빈을 스프링 빈으로 등록하여 개발자가 메트릭뿐만 아니라 추적까지 함께할 수 있도록 지원한다

[스프링 부트 metrics autoconfiguraiton](./spring%20boot%20observability.md#compositemeterregistryautoconfiguration)

[스프링 부트 observability autoconfiguration](./spring%20boot%20observability.md#observationautoconfiguration)

어노테이션 기반 방식으로 메트릭을 측정할 수 있도록 각 어노테이션에 대한 aspect 구현체를 마이크로미터에서 제공하는데, micrometer-observation의 @Observed에 해당하는 aspect(ObservedAspect)는 스프링 부트 자동 구성에 의해 빈으로 등록된다

반면, micrometer metrics의 @Timed와 @Counted 어노테이션의 aspect는 자동 구성 대상의 포함이 되지 않기 때문에 TimedAspect와 CountedAspect 클래스를 필요에 따라 빈으로 등록해야 한다

```java
@Configuration(
        proxyBeanMethods = false
)
public class ObservationConfig {

    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
```


## actuator, prometheus configuration

프로메테우스는 기본적으로 pull 기반 방식으로 데이터를 수집한다 

즉, 프로메테우스 서버에서 대상(스프링 부트)에게 요청(액추에이터 엔드포인)을 보낸 후 응답 받은 데이터(애플리케이션 메트릭)를 수집(scrape)한 다음 시계열 데이터로 가공한다  

스프링 부트 액추에이터는 `/actuator` `/actuator/health`를 제외한 다른 엔드포인트를 노출하지 않기 때문에 프로메테우스가 스프링 부트의 데이터를 수집할 수 있도록 엔드포인트를 노출하도록 설정해야 한다

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, prometheus

# 프로메테우스의 스크랩 요청을 로그로 출력한다 
logging:
  level:
    org.springframework.web: DEBUG
```

그 다음 프로메테우스의 스크랩 동작을 정의할 prometheus.yml 파일을 작성한다

```yaml
global:
  external_labels:
    monitor: 'spring-boot-actuator-prometheus'

scrape_configs:

  -
    job_name: 'spring-boot-metrics'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s

    static_configs:
      - targets: ['spring-boot:8080']
```


## metrics measurement

스프링 부트 애플리케이션에서 메트릭 측정은 요구사항에 따라 다음과 같이 선택할 수 있다

측정 방식: 프로그래밍 vs 어노테이션

측정 도구: micrometer observation vs micrometer metrics (micrometer-core)

앞서 스프링 부트 액추에이터 스타터는 micrometer observation 의존성을 포함한다고 언급했는데, 이 모듈은 마이크로미터의 tracing api와 metrics(core) api를 통합한다

따라서 필요에 따라 observation과 metrics에서 제공하는 기능을 선택해서 애플리케이션의 메트릭을 측정할 수 있으며 두 모듈은 다음과 같은 차이점을 가진다

| 구분       | micrometer observation                                       | micrometer metrics                                  |
|----------|--------------------------------------------------------------|-----------------------------------------------------|
| 사용       | 마이크로서비스/모듈 모놀리식 (메서드 성능 모니터링, opentelemetry와의 통합, 비동기 코드 추적) | 모놀리스/전통적인 메트릭 수집 방식 (api 호출 횟수, 메서드 실행 시간, 리소스 사용률) |
| 프로그래밍 방식 | ObservationRegistry                                          | MeterRegistry                                       |
| 어노테이션 방식 | @Observed (autoconfiguration 적용)                             | @Timed, @Counted (autoconfiguration 적용 X)           |
| 측정 대상    | timer, counter, gauge + trace(span)                          | timer, counter, gauge, distributionSummary          |
| 데이터 표현   | high/low cardinality, span, event 등                          | metric-name, tags                                   |
| 추적 지원    | distributed tracing, context propagation 지원                  | X                                                   |

### annotation-based metrics measurement 

#### micrometer observation

```java
@Observed(name = "user.name", contextualName = "getting-user-name", lowCardinalityKeyValues = {"service", "user"})
String getUsername(Long userId) {
    return "spring-man";
}
```


#### micrometer metrics

```java
@Timed(value = "user.creation.timer", percentiles = {0.3, 0.5, 0.95}, description = "user creation process timer")
public void createUser() {
    Random processTime = new Random();
    try {
        Thread.sleep(processTime.nextLong(100, 1000));
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}
```
