---
layout: default
title:
---

#### index
- [spring boot actuator logger level configuration](#spring-boot-actuator-logger-level-configuration)
- [/actuator/loggers](#actuatorloggers)


## spring boot actuator logger level configuration

스프링 부트 액추에이터는 애플리케이션 런타임에 설정된 로그 레벨을 확인하고 다시 설정할 수 있는 기능을 제공한다

이를 통해 애플리케이션을 재배포하지 않고도 동적으로 특정 로그의 레벨을 변경할 수 있다

전체 로거 목록 또는 각 로거의 구성(명시적인 구성 또는 로깅 프레임워크에 의한 구성)을 볼 수 있으며 로그 레벨은 다음 중 하나를 가진다

TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF, null(로거에 대한 구성이 없을 때)


## `/actuator/loggers`

스프링 부트 액추에이터에서 자동 구성하는 `/actuator/loggers` 엔드포인트를 노출하여 애플리케이션에 등록된 모든 로거 목록을 조회할 수 있다

configuredLevel은 수동으로 설정된 로그 레벨을 의미하고, effectiveLevel은 실제 적용된 로그 레벨을 의미한다

```shell
curl localhost:8080/actuator/loggers | jq
```

```json
{
  "levels": [
    "OFF",
    "ERROR",
    "WARN",
    "INFO",
    "DEBUG",
    "TRACE"
  ],
  "loggers": {
    "ROOT": {
      "configuredLevel": "INFO",
      "effectiveLevel": "INFO"
    },
    "_org": {
      "effectiveLevel": "INFO"
    },
    "_org.springframework": {
      "effectiveLevel": "INFO"
    },
    "_org.springframework.web.servlet": {
      "effectiveLevel": "INFO"
    },
    "hansanhha": {
      "effectiveLevel": "INFO"
    },
    "hansanhha.ServerApplication": {
      "effectiveLevel": "INFO"
    }
  }
}
```

아래의 파일은 /actuator/loggers 엔드포인트로 조회한 spring boot starter web과 actuator, micrometer-tracing을 의존성을 가진 애플리케이션에 등록된 전체 로거에 대한 결과 목록이다  

[스프링 부트 애플리케이션 로거 목록](./actuator%20loggers%20list.json)

또는 `/actuator/loggers/{package name}`으로 특정 로거를 조회할 수도 있다

```shell
curl localhost:8080/actuator/loggers/org.springframework.web
```

특정 로그의 레벨을 설정하려면 `/actuator/loggers/{pacakge name}` 경로로 아래와 같은 요청 본문(json)을 담은 post 요청을 보내면 된다

만약 로거 레벨을 기본 설정으로 되돌리고 싶으면 configuredLevel의 값을 null로 설정하고 요청을 보내면 된다

```shell
curl -X POST localhost:8080/actuator/loggers/hansanhha \
-H "Content-Type: application/json" \
-d '{"configuredLevel": "DEBUG"}'
```

위의 curl 명령으로 변경된 hansanhha 패키지에 속한 로거의 레벨을 다시 조회하면 아래와 같다

```shell
curl localhost:8080/actuator/loggers/hansanhha | jq
 
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    52    0    52    0     0  17054      0 --:--:-- --:--:-- --:--:-- 17333
{
  "configuredLevel": "DEBUG", # 수동으로 설정한 레벨
  "effectiveLevel": "DEBUG"   # 실제 적용된 레벨
}
```







