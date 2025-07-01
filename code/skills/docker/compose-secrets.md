---
layout: default
title:
---

[docker compose secrets](#docker-compose-secrets)

[example](#example)


## docker compose secrets

도커 컴포즈는 secrets를 통해 민감한 데이터(데이터베이스 연결 정보, API 키, 인증서 등)를 안전하게 관리할 수 있도록 한다

민감한 정보를 secrets 대신 도커 컴포즈의 환경 변수에 저장하면 `docker compose config --environment` 등을 통해 접근할 수 있게 되어 의도치 않게 노출될 수 있다

secrets는 하나의 파일로 컨테이너의 `/run/secrets/<secret_name>` 경로에 마운트된다


## example

민감한 데이터를 다음 파일을 아래와 같이 정의한다

```text
MYSQL_ROOT_PASSWORD=1234
MYSQL_ALLOW_EMPTY_PASSWORD=123
MYSQL_RANDOM_ROOT_PASSWORD=12
```

도커 컴포즈 파일의 최상단에 secrets 속성을 사용하여 서비스에서 사용할 secrets 들을 정의한다 

```yaml
# 서비스에서 사용할 secrets 정의
secrets:

  # secrest 파일 정의
  db_credentials:
    file: ./config/db_credentials.txt

  # 환경변수로부터 정의할 수도 있다
  mysql_password:
    environment: MYSQL_CREDENTIALS
```

서비스에서 secrets 속성을 통해 최상단에 정의된 secrets를 선택적으로 참조하고, `/run/secrets/<secret_name>` 경로를 사용하여 값을 주입한다 

환경 변수에 값을 주입한다고 하더라도 secrest를 사용하면 `docker compose config --environment`로 해당 값을 확인할 수 없다

```yaml
services:

  mysql:
    image: mysql:8.4.0
    platform: linux/amd64
    profiles:
      - db
      - example
    environment:
      - MYSQL_ROOT_PASSWORD=/run/secrets/db_credentials
      - MYSQL_ALLOW_EMPTY_PASSWORD=/run/secrets/db_credentials
      - MYSQL_RANDOM_ROOT_PASSWORD=/run/secrets/db_credentials
    secrets:
      - db_credentials
```
