---
layout: default
title:
---

[docker compose environment variables](#docker-compose-environment-variables)

[environment attribute](#environment-attribute)

[env_file attribute](#env_file-attribute)

[interpolation](#interpolation)

[.env file](#env-file)

[--env-file flag](#--env-file-flag)

[pre-defined environment variables in docker compose](#pre-defined-environment-variables-in-docker-compose)


## docker compose environment variables

컴포즈 파일에서 각 서비스에 대한 환경 변수를 직접 설정, interpolation 또는 환경 파일을 통해서 설정할 수 있다

참고로 환경 변수는 외부에서 확인할 수 있기 때문에 민감한 정보는 환경 변수 대신 [secrets](./docker%20compose%20secrets.md)에 저장하는 것이 좋다


## environment attribute

다음과 같이 environment 속성을 활용하여 특정 환경 변수와 값을 컴포즈 파일에서 직접 설정할 수 있다

```yaml
services:
  user:
    build: ./user
    container_name: spring-user-app
    ports:
      - "8083:8080"
    profiles: [dev, user]
    environment:
      # 직접 환경 변수와 값을 할당하는 두 가지 방식
      ACTIVE_PROFILES: prod, dev
#      - ACTIVE_PROFILES=prod, dev
```

또는 아래와 같이 환경 변수만 명시하고 컴포즈를 실행할 때 값을 전달할 수 있다

명시한 환경 변수의 값은 `docker compose run -e ACTIVE_PROFILES=prod,staging user`와 같이 터미널에서 값을 전달하거나 환경 변수 파일을 지정함으로써 설정할 수 있다

```yaml
services:
  user:
    build: ./user
    container_name: spring-user-app
    ports:
      - "8083:8080"
    profiles: [dev, user]
    environment:
    
    # 환경 변수만 설정하는 방식, 쉘에서 값을 전달한다
    # docker run -e VARIABLE ... 과 동일한 방식으로 동작한다
    # docker compose run -e ACTIVE_PROFILES=prod,staging user
#      - ACTIVE_PROFILES
    
    # 아래처럼 선언하면 쉘 또는 .env 파일에서 값을 설정하지 않은 경우 컴포즈가 경고한다
#      - ACTIVE_PROFILES=${USER_APP_ACTIVE_PROFILES}
```


## env_file attribute

env_file 속성을 활용하여 `.env` 파일을 서비스에 전달할 수 있다

환경 변수 파일은 서비스 간 중복되는 environment 블록을 줄일 수 있고, 컴포즈 파일로부터 환경 변수 구성을 분리할 수 있다

또한 프로젝트 디렉토리에 두지 않아도 되기 때문에 더욱 안전한 방식으로 환경 변수들을 관리할 수 있다

```yaml
  user:
    build: ./user
    container_name: spring-user-app
    ports:
      - "8083:8080"
    profiles: [dev, user]
    
    # 여러 개의 .env 파일을 명시할 수 있다
    # 각 파일은 순서대로 평가되며, 이전 파일의 값을 재정의할 수 있다
    env_file:
      - user.env
      - .env
```


## interpolation

환경 변수의 값을 외부로부터 주입받을 변수를 선언하는 것을 interpolation(보간)이라고 한다

아래와 같이 `${VAR}` 또는 `$VAR`로 주입받을 환경 변수에 명시하며, 외부의 쌍따옴표로 감싸진 값이나 감싸지지 않은 값 (e.g: `"hello"` 또는 `hello`)을 받을 수 있다

[interpolation syntax](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/#interpolation-syntax)

```yaml
services:
  example-profile-app:
    image: hansanhha/example-spring-app

    # interpolation: ${VAR} 또는 $VAR 형태로 선언하여 외부로부터 변수의 값을 주입받는 기능
    # ${VAR:-default}: VAR의 값이 설정되지 않은 경우 defalut 값 사용
    container_name: ${EXAMPLE_PROFILE_APP_NAME:-default_example_profile_app}
```

interpolate 변수는 다음의 방법으로 설정할 수 있다
- 쉘 환경 변수
- `.env` 파일 또는 지정된 `--evn-file` 파일에 정의된 변수

참고로 동일한 이름으로 여러 파일에 변수를 정의한 경우 우선순위에 따라 재정의된다

`docker compose config --environment` 명령어를 사용하여 실행 중인 컴포즈 모델의 변수와 값들을 확인할 수 있다

## .env file

`.env` 파일은 `docker compose up` 실행 시 interpolation할 변수(키-값 쌍)들, 환경 변수들을 정의한 텍스트 파일을 말한다

프로젝트의 루트 디렉토리 두거나 외부 디렉토리에 두고 `--env-file` 플래그를 통해 참조할 수 있다 (docker compose cli 전용 기능)

[.env file syntax](https://docs.docker.com/compose/how-tos/environment-variables/variable-interpolation/#env-file-syntax)


## --env-file flag

user 서비스는 아래와 같이 환경 변수 `ACTIVE_PROFILES`에 `${USER_APP_ACTIVE_PROFILES}` 라는 interpolate 변수를 선언해서 외부로부터 주입받고 있다

```yaml
services:
  user:
    build: ./user
    container_name: spring-user-app
    ports:
      - "8083:8080"
    profiles: [dev, user]
    environment:
      - ACTIVE_PROFILES=${USER_APP_ACTIVE_PROFILES}
```

개발자는 개발/스테이징/운영 환경 별로 user 서비스의 `ACTIVE_PROFILES` 값을 다르게 지정하고자 아래와 같이 `./config` 경로에 각각 `.env.dev` `.env.staging` `.env.prod` 파일을 만들고 환경 변수를 정의한다

```text
USER_APP_ACTIVE_PROFILES=dev
```
```text
USER_APP_ACTIVE_PROFILES=staging
```
```text
USER_APP_ACTIVE_PROFILES=dev
```

그리고 커맨드 라인의 `--env-file` 플래그를 통해 특정 환경 변수 파일을 설정하여 user 서비스의 `ACTIVE_PROFILES` 변수 값을 동적으로 변경한다

```shell
docker compose --env-file ./config/.env.dev up user
docker compose --env-file=./config/.env.prod up user
docker compose --env-file=./config/.env.staging up user

# 아래와 같이 여러 개의 환경 변수 파일을 지정할 수도 있다
docker compose --env-file ./config/.env.staging --env-file ./config/.env.dev up user
```


## pre-defined environment variables in docker compose

[docker docs](https://docs.docker.com/compose/how-tos/environment-variables/envvars/)
