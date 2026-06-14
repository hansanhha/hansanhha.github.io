---
layout: default
title:
---

[docker compose profiles](#docker-compose-profiles)

[activate specific/multiple profiles](#activate-specificmultiple-profiles)

[auto-starting profiles and dependency resolution](#auto-starting-profiles-and-dependency-resolution)

[stop specific profiles](#stop-specific-profiles)


## docker compose profiles

특정 서비스를 선택적으로 활성화할 때 profiles 속성을 사용할 수 있다

profiles 속성은 배열로 관리되며 `[a-zA-Z0-9][a-zA-Z0-9_.-]+`을 값으로 가질 수 있다

별도로 지정되지 않은 서비스는 기본적으로 활성화된다

아래의 example-spring-app 서비스는 example 프로파일에서만 활성화된다

```yaml
services:

  example-spring-app:
    image: hansanhha/example-spring-app
    ports:
      - "9090:8080"
    profiles: [example]
```


## activate specific/multiple profiles

`--profile` 옵션 또는 환경변수 `COMPOSE_PROFIELS`에 활성화시킬 특정 프로파일을 명시한다

```shell
docker compose --profile=example up

COMPOSE_PROFILES=example docker compose up
```

또는 `--profile` 옵션을 여러 번 사용하거나 `COMPOSE_PROFILES` 환경변수에 콤마로 구분하여 여러 개의 프로파일을 활성화시킬 수 있다

```shell
docker compose --profile=example --profile=dev up

COMPOSE_PROFILES=example,dev docker compose up
```


## auto-starting profiles and dependency resolution

```shell
docker compose up example-profile-app
```

위와 같이 커맨드라인에 `--profile ` 옵션없이 특정 프로파일이 지정된 서비스를 시작하면 해당 프로파일이 자동으로 적용되고, 서비스가 의존하는 다른 서비스가 해당 조건에 부합될 경우 같이 시작된다
- 서로 프로파일을 공유하는 경우
- profiles 속성을 사용하지 않거나 명시적으로 지정된 프로파일이 매칭되는 경우

**서비스에 지정된 프로파일을 활성화시키지만 프로파일에 속한 다른 서비스들은 실행되지 않고 지정된 서비스와 조건에 부합되는 의존 서비스만 실행된다**

아래의 `example-profile-app` 서비스는 `mysql` 서비스를 의존하면서 example 프로파일이 지정되어 있다

이와 다르게 mysql 서비스의 프로파일은 db로 지정되어 있는 상태에서 `docker compose up example-profile-app` 명령을 실행하면 mysql 서비스가 활성화되지 않아서 실행이 실패한다

```yaml
services:
  
  mysql:
    image: mysql:8.4.0
    platform: linux/amd64
    profiles:
      - db


  example-profile-app:
    image: hansanhha/example-spring-app
    platform: linux/amd64
    ports:
      - "9090:8080"
    depends_on:
      - mysql
    profiles:
      - example  
```

example-profile-app이 의존하는 mysql 서비스를 함께 실행하기 위한 세 가지 방법이 있다
- mysql의 profiles 속성 제거: 항상 mysql 서비스를 실행하도록 한다
- mysql의 profiles 값 추가: example 프로파일을 추가하여 example-profile-app 실행 시 함께 실행되도록 한다
- 커맨드라인에 dev 프로파일 값 지정: 프로파일을 수정하지 않고 example-profile-app 실행 시 dev 프로파일을 활성화한다

```shell
# example-spring-app을 실행하면서 example 프로파일을 활성화시키고, 명시적으로 dev 프로파일을 활성화한다
docker compose --profile=dev example-spring-app
```

```yaml
# mysql 서비스의 프로파일 속성에 example을 추가한다 
services:
  mysql:
    image: mysql:8.4.0
    platform: linux/amd64
    profiles:
      - db
      - example
```


## stop specific profiles

down 명령어를 사용해서 `--profile` 플래그 또는 `COMPOSE_PROFILES`에 지정된 특정 프로파일의 실행을 멈추고 서비스를 삭제한다

[auto-starting profiles and dependency resolution](#auto-starting-profiles-and-dependency-resolution) 메커니즘과 동일하게 down 대상으로 지정된 프로파일의 서비스가 의존하는 서비스가 조건에 부합하는 경우 해당 서비스도 중지하고 삭제된다
- 서로 프로파일을 공유하는 경우
- profiles 속성을 사용하지 않거나 명시적으로 지정된 프로파일이 매칭되는 경우

```shell
docker compose --profile=dev down

COMPOSE_PROFILES=dev docker compose down
```