---
layout: default
title:
---

[docker compose](#docker-compose)

[compose application model: platform, service, project](#compose-application-model-platform-service-project)

[how compose works](#how-compose-works)

[use basic docker compose](#use-basic-docker-compose)

[secrets](#secrets)

[networking](#networking)


## docker compose

도커 컴포즈는 다중 컨테이너 애플리케이션을 정의하고 실행하는 도구다

전체 애플리케이션 스택(웹 서버, 데이터베이스, 메시지 큐 등)에 대한 제어, 서비스, 네트워크, 볼륨 등에 대한 구성을 단일 yaml 파일(docker.yml, docker-compose.yml)에서 쉽게 정의할 수 있도록 한다 

또한 하나의 명령어로 구성 파일에 명시된 모든 서비스들을 생성하고 시작할 수 있다

컴포즈는 운영, 스테이징, 개발, 테스트, ci 워크플로우 등 모든 환경에서 동작하며 애플리케이션에 대한 라이프사이클을 관리하는 명령어를 제공한다
- 다중 서비스 시작/중지/리빌드
- 실행 중인 다중 서비스 상태 확인
- 실행 중인 다중 서비스 로그 출력 스트림
- 서비스에서 일회성 명령 실행

[common use cases of docker compose](https://docs.docker.com/compose/intro/features-uses/)


## compose application model: platform, service, project

### platform

컴포즈 사양에서 언급되는 플랫폼은 일반적으로 애플리케이션이 실행되는 환경을 의미한다

컴포즈는 다양한 플랫폼에서 컨테이너를 실행할 수 있도록 설계되었으며 플랫폼은 특정 운영체제나 하드웨어뿐만 아니라 컨테이너를 실행하고 관리하는 다양한 환경(docker engine/swarm, kubernetes, cloud, ci/cd 등)을 포함한다

즉, 같은 compose.yml 파일을 사용하더라도 로컬 도커에서 실행될 수 있고 쿠버네티스 같은 환경에서도 실행할 수 있다

이러한 플랫폼 차이를 고려하여 컴포즈는 네트워크, 볼륨, 설정 데이터(configs, secrets) 등을 각 플랫폼에 맞게 추상화하여 정의할 수 있도록 설계되었다

### platforms

#### docker engine

로컬 환경에서 실행되는 도커 데몬 

일반적인 `docker run`, `docker compose up` 명령어가 사용되는 환경

#### docker swarm

도커의 오케스트레이션 기능을 제공하는 스웜 모드 

여러 개의 노드에서 컨테이너를 클러스터링하여 실행

#### kubernetes

쿠버네티스 클러스터에서 실행되는 컨테이너 오케스트레이션 환경

kompose 같은 도구를 사용해 compose.yml 파일을 쿠버네티스 manifest로 변환할 수 있다

#### cloud providers
 
aws ecs(elastic container service)

azure container instances(aci)

google cloud run 등

#### ci/cd

github actions, gitlab ci/cd, jenkins 등의 ci/cd 파이프라인에서 실행되는 환경

### abstraction by platform

#### network abstraction

docker engine에서는 기본적으로 bridge, host, none과 같은 네트워크를 제공한다

swarm에서는 overlay 네트워크를 사용해 여러 노드 간의 통신을 지원한다

kubernetes에서는 pod 단위로 네트워크가 설정되며 서비스 디스커버리 방식이 다르다

컴포즈에서 네트워크를 설정할 때 특정 플랫폼을 위한 옵션을 추가할 수도 있고 플랫폼에 따라 자동으로 적절한 네트워크 구성을 선택할 수 있다

#### volumes abstraction

로컬 환경(docker engine)에서는 일반적인 파일 시스템 볼륨(`/var/lib/docker/volumes`)을 사용한다

클라우드 환경에서는 s3, azure blob storage 같은 외부 저장소와 연계할 수 있다

쿠버네티스 환경에서는 PersistenceVolumeClaim (PVC)을 사용한다

컴포즈의 볼륨은 이러한 차이를 숨기고 각 플랫폼에서 적절한 방식으로 데이터를 저장하고 공유할 수 있도록 설계되었다

#### environment config(configs, secrets) abstraction

로컬 환경에서는 단순히 파일을 마운트하는 방식을 사용한다

스웜에서는 docker secrets 명령어를 사용하여 클러스터 전역에서 관리할 수 있다

쿠버네티스에서는 ConfigMap과 Secret을 사용한다

컴포즈는 configs와 secrets 개념을 사용하여 플랫폼별 차이를 추상화하고 사용자가 같은 방식으로 데이터를 정의할 수 있도록 한다

### service

도커 컴포즈에서 애플리케이션의 구성 요소는 서비스로 정의되는데, 서비스는 동일한 컨테이너 이미지와 설정을 사용하여 한 개 이상의 컨테이너로 실행되는 추상적인 개념이다

컴포즈는 네트워크를 플랫폼 기능의 추상화 계층으로 정의하며 같이 연결된 서비스들의 컨테이너 간에 ip 라우팅을 설정하여 서비스들이 네트워크를 통해 통신할 수 있도록 한다

서비스들의 데이터를 볼륨에 저장하고 공유할 수 있다. 컴포즈 사양에서 볼륨은 파일 시스템 마운트의 추상적인 고급 개념으로 다양한 플랫폼별 옵션과 함께 사용할 수 있다

일부 서비스는 실행 시점(runtime) 또는 플랫폼(platform)에 따라 다른 설정 데이터가 필요할 수 있는데, 이를 위해 configs(설정 파일)을 사용할 수 있다

또한 설정 데이터 중에서도 보안에 민감한 데이터는 secrets를 통해 정의할 수 있다

이러한 volumes, configs, secrets는 상위 수준에서 선언하거나 서비스 수준에서 플랫폼별 정보를 추가하여 세부적인 설정을 할 수 있다

### project

프로젝트는 특정 플랫폼에서 애플리케이션을 하나의 독립적인 배포 단위로 정의한 것이다

프로젝트의 이름은 컴포즈 구성 파일의 최상위 name 속성에서 설정되며, 여러 개의 애플리케이션 리소스를 그룹화하고 다른 애플리케이션 또는 동일한 컴포즈 스펙을 사용하는 다른 배포 환경과 분리할 수 있다

도커 컴포즈는 사용자가 직접 프로젝트 이름을 지정하고 오버라이드할 수 있도록 지원한다

이를 통해 동일한 compose.yml 파일 사용하더라도 서로 다른 이름을 지정하여 동일한 인프라에서 여러 번 배포할 수 있다

어떤 플랫폼에서 리소스를 생성하면 리소스 이름 앞에 프로젝트 이름이 자동으로 붙으며 `com.docker.compose.prject` 레이블을 통해 프로젝트 소속이 관리된다


## how compose works

#### 1. 구성 파일 작성

도커 컴포즈는 워킹 디렉토리에 위치한 `compose.yml` `compose.yaml` 파일을 지원하며 하위호환성을 위해 `docker-compose.yml` `docker-compose.yaml` 파일도 지원한다

두 파일이 동시에 존재하는 경우 `compose.yml` 파일이 우선순위를 가진다

구성 파일에서 각 컨테이너를 하나의 서비스로 정의하고 네트워크 및 볼륨 등을 설정할 수 있다

#### 2. compose 명령어를 사용하여 컴포즈 애플리케이션 모델 관리

`docker compose` 명령어를 사용하여 컴포즈 파일에 구성된 서비스를 관리한다

`docker compose up`: 컴포즈 파일에 정의된 모든 서비스 시작

`docker compose down`: 실행 중인 모든 서비스 중지 및 삭제

`docker compose logs`: 실행 중인 컴포즈 컨테이너 로그 출력

`docker compose ps`: 모든 서비스에 대한 상태 출력

#### 3. 서비스 기반 실행

컨테이너를 개별적으로 실행하는 것이 아니라 서비스 단위로 실행한다

또한 동일한 서비스 내에서 여러 개의 컨테이너를 생성하여 스케일링할 수도 있다

#### 4. 네트워크 자동 설정

기본적으로 서비스 간 통신을 위해 독립적인 네트워크가 자동 생성된다

같은 컴포즈 프로젝트 내의 컨테이너들은 서비스 이름으로 서로에게 접근할 수 있다

#### 5. 데이터 저장 및 공유 (볼륨)

볼륨을 설정하여 컨테이너의 종료 여부와 상관없이 컨테이너의 데이터를 유지할 수 있다

#### 6. 환경 변수 및 설정 관리

.env 파일을 통해 환경 변수를 설정할 수 있으며, configs 및 secrets 같은 기능을 활용해 플랫폼별 설정을 적용할 수 있다


## use basic docker compose

두 개의 스프링 부트 애플리케이션을 준비하고 각각 도커 파일 설정

이미 빌드된 도커 이미지(라이브러리 같은 공식 이미지)를 사용하면 도커 컴포즈는 도커 파일을 필요로 하지 않지만 애플리케이션은 이미지를 직접 빌드해야 하므로 각 모듈에 대한 도커 파일을 필요로 한다

### build spring boot application

레디스를 통해 단순히 카운트 값을 증가/감소시키는 [counting](../example/compose-application/counting) 애플리케이션과 문자열을 출력하는 [greeting](../example/compose-application/greeting) 애플리케이션을 만들고 아래와 같이 도커 파일을 준비한다

```dockerfile
# 사전에 bootJar 빌드 필요

FROM openjdk:21-jdk-slim
LABEL authors="hansanhha"

WORKDIR /app
COPY ./build/libs/compose-greeting-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### write compose.yml

프로젝트 최상단에 도커 컴포즈에 포함시킬 레디스 서비스를 infra.yml 파일에 정의한다

```yaml
services:
  redis:
    image: redis:alpine
    container_name: redis
    ports:
      - "6379:6379"
```

그 다음 같은 경로에서 compose.yml 파일을 아래와 같이 작성한다

include 속성을 infra.yml 경로와 함께 최상단에 명시하고 두 개의 스프링 부트 애플리케이션 서비스를 정의한다

include 속성은 도커 컴포즈의 서비스 간 파일 분리를 가능하게 한다

```yaml
include:
  - infra.yml

services:

  counting:
    build: ./counting
    container_name: spring-counting-app
    ports:
      - "8080:8080"
    # ./counting/src 파일이 변경되면 이미지 재빌드 및 컨테이너 재시작
    develop:
      watch:
        - action: rebuild
          path: ./counting/src

  greeting:
    build: ./greeting
    container_name: spring-greeting-app
    ports:
      - "8081:8080"
    # ./greeting/src 파일이 변경되면 이미지 재빌드 및 컨테이너 재시작
    develop:
      watch:
        - action: rebuild
          path: ./greeting/src
```

### run docker compose service

도커 컴포즈 파일에 명시된 서비스에 대한 컨테이너를 만들고 실행하는 아래의 명령어를 실행한다 

```shell
# --watch 플래그로 소스 코드 변경 감지 기능 활성화
docker compose up --watch
```

두 개의 웹 브라우저 세션 창을 띄우고 `localhost:8080/api/hits`와 `localhost:8081/api`을 입력하여 두 개의 애플리케이션이 정상적으로 가동되는 것을 확인한다

컴포즈 컨테이너를 종료시키려면 아래의 명령어 또는 ctrl + c를 눌러 종료시킨다

```shell
docker compose stop

# 종료 및 삭제
docker compose down
```


## secrets


## networking