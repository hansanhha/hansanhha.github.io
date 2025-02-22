#### index
- [spring boot actuator metrics autoconfiguration](#spring-boot-actuator-metrics-autoconfiguration)


## spring boot actuator metrics autoconfiguration

스프링 부트 액추에이터는 클래스 패스에 micrometer 구현체가 존재하는 경우 CompositeMeterRegistry를 자동 구성하고 해당 registry에 각 구현체를 삽입한다

[지원되는 전체 모니터링 시스템 목록](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.export)




## 