---
layout: default
title:
---

[docker image](#docker-image)
- [image layer](#image-layer)
- [image build & caching mechanism](#image-build--caching-mechanism)
- [multi-stage build](#multi-stage-build)
- [environment variables](#environment-variables)
- [image name (image reference)](#image-name-image-reference)

[docker file](#docker-file)
- [docker file example](#example-for-docker-file)
- [docker file commands](#docker-file-commands)


## docker image

이미지는 애플리케이션과 모든 의존성(라이브러리, 소스 코드, 설정 파일, 환경 변수)을 패키징하며 실행 환경을 정의한다

도커의 이미지는 OCI(Open Container Initiative) 표준을 따라서 다양한 OS 플랫폼에서 실행할 수 있다

### image type

#### authorized publisher

오라클, IBM 같은 신뢰할 수 있는 대기업에서 배포하는 이미지

#### official image

주로 오픈소스 소프트웨어 이미지, 해당 프로젝트 개발 팀과 도커가 함께 이미지를 관리한다

#### ordinary image

도커 이미지 중에서 기본적인 기능을 갖춘 이미지

특정 언어의 런타임/라이브러리/운영 체제 등 애플리케이션 실행 시 필요한 환경을 포함하며 다양한 환경에 맞게 커스터마이징할 수 있다

#### golden image

특정 조직이나 프로젝트의 특정 버전의 애플리케이션/라이브러리/설정 파일 등 필수 구성 요소와 설정이 모두 포함된 표준화된 이미지를 말한다

모든 컨테이너가 동일한 환경에서 시작되도록 보장한다

일반 이미지를 사용하여 커스터마이징을 할 수 있지만, 골든 이미지를 사용하면 모든 배포 환경에서 동일한 조건을 유지할 수 있다


## image layer

이미지 레이어는 도커 이미지를 구성하는 기본 단위로 하나의 파일을 말한다

하나의 이미지는 한 개 이상의 레이어를 가지며 이를 기반으로 컨테이너를 생성하고 실행한다

도커는 이미지를 통해 컨테이너를 생성할 때 이미지 레이어들을 조립해서 컨테이너 내부 파일 시스템을 만든다

### raed-only layer

이미지 레이어는 수정할 수 없는 읽기 전용 파일 형태이다

컨테이너가 실행되면 이미지 레이어 위에 읽기-쓰기(writable layer)가 추가된다

### layered structure

각 레이너는 이전 레이어를 기반으로 하여 변경사항만 포함한다

레이어는 계층적으로 쌓이며 하위 레이어는 상위 레이어에 포함된다

### caching

레이어는 고유 해시값(SHA-256)을 기반으로 관리되며 동일한 레이어는 여러 이미지에서 재사용될 수 있다

-> 동일한 베이스 이미지를 사용하는 두 개의 도커 이미지는 해당 베이스 이미지를 공유할 수 있다

### immutability

이미지 레이어는 불변하며 한 번 생성된 레이어는 수정될 수 없다

새로운 변경사항이 필요하면 새로운 레이어가 추가된다

### union file system

도커는 UnionFs(OverlayFS, AUFS 등)를 사용하여 여러 레이어를 하나의 파일 시스템으로 결합한다

각 레이어는 별도의 디렉토리로 저장되지만 UnionFs는 이러한 디렉토리를 하나의 파일 시스템처럼 보여주도록 결합한다

### layer type

#### base image layer

레이어들의 기반 레이어로 주로 운영 체제 수준의 파일 및 라이브러리를 포함한다

`ubuntu:20.04` `alpine:3.17` 등

#### command layer

도커 파일의 각 `RUN` `COPY` `ADD` 명령어는 출력 결과에 따른 새로운 레이어를 생성한다

#### metadata layer

`CMD` `ENTRYPOINT` `ENV`와 같은 명령어는 실행 시 사용할 메타데이터를 포함한다

#### writable layer

읽기 전용 레이어들로 구성된 이미지를 기반으로 컨테이너를 생성 및 실행하면 추가되는 유일한 읽기-쓰기 레이어다

컨테이너가 삭제되면 이 레이어도 같이 삭제된다 - 컨테이너에 쓴 데이터는 모두 삭제(휘발)되므로 상태를 관리해야 하는 경우 외부 볼륨이나 데이터베이스를 사용하는 것이 좋다


## image build & caching mechanism

```shell
docker build -t my-image .
```

이미지는 도커 파일(Dockerfile)을 통해 빌드되며, 빌드된 이미지를 통해 컨테이너를 생성 및 실행한다

이미지 빌드 시 도커 엔진은 각 도커 파일의 명령어(RUN, COPY, ADD 등)에 따른 실행 상태(파일 시스템 변화, 환경 변수, 패키지 설치 등)를 기반으로 이미지 레이어를 생성한다

도커는 각 명령어를 통해 해시 값을 생성하고 이 값을 통해 새로 생성된 이미지 레이어를 캐시한다  

이후 빌드를 했을 때 해시 값이 이전과 같다면 기존에 캐시된 레이어를 재사용하여 빌드 속도를 최적화한다

만약 새로운 변경(명령어 자체, 명령어 환경, 파일 시스템 변경 등)으로 인해 해시 값이 변경되면 캐시 무효화(cache busting)가 발생하여 변경된 레이어 이후 모든 명령어는 재실행되고 새로운 레이어와 해시 값을 생성하여 다시 캐시한다   

마지막으로 모든 명령어를 실행하면 생성된 모든 레이어를 결합하여 하나의 완전한 이미지를 생성한다 

### image layer creation workflow

이미지는 다단계 레이어(layered-structure)로 구성되며 각 레이어는 특정 명령어(RUN, COPY 등)에서 생성된다

#### 1. base image layer

`FROM ubuntu:latest`와 같이 FROM 명령어와 base 이미지를 통해 최초 레이어를 생성한다

지정된 이미지가 로컬에 없는 경우 레지스트리로부터 다운로드받는다

base image layer는 일반적으로 rootfs (루트 파일 시스템)를 포함한다

#### 2. intermediate layers 

RUN, COPY, ADD 등과 같은 명령어가 실행될 때마다 새로운 읽기 전용 레이어를 추가한다

#### 3. complete image

모든 레이어가 병합된 후 최종적으로 읽기 전용 이미지가 완성된다

#### 4. writable layer

이미지를 기반으로 컨테이너를 생성하면 읽기-쓰기 가능한 레이어(컨테이너 레이어)가 생성된다

```text
---------------------------------
|  container layer (writable)   |
|-------------------------------|
|  image layer 3 (read-only)    |  <- `RUN apt-get install`
|-------------------------------|
|  image layer 2 (read-only)    |  <- `COPY . /app`
|-------------------------------|
|  image layer 1 (read-only)    |  <- `FROM ubuntu:latest`
---------------------------------
```


## multi-stage build

도커의 FROM 명령은 빌드 스테이지를 시작하는 명령어로 도커 파일 내에 FROM 명령을 여러 번 사용하면 각각 새로운 빌드 스테이지를 시작한다

각 스테이지는 자신의 베이스 이미지로 시작하며 해당 스테이지 내에서 실행된 명령어들은 해당 스테이지의 이미지 레이어로 쌓인다

스테이지 간에는 기본적으로 독립적이지만 `COPY --from=<stage name>` 또는 `COPY --from<number>` 명령을 사용하여 이전 스테이지의 결과물을 현재 스테이지로 복사할 수 있다

도커는 마지막 스테이지의 결과물을 최종 이미지로 사용한다 -> 해당 스테이지에서 구성된 파일 시스템(디렉토리, 파일 등)과 환경 변수 및 설정(ENV, WORKDIR, USER 등), 메타데이터 실행 명령(CMD, ENTRYPOINT, EXPOSE 등)

또는 `--target` 옵션을 사용하면 특정 스테이지까지 빌드할 수도 있다

```dockerfile
# 첫 번째 빌드 스테이지 (빌드 단계)
# AS 키워드로 스테이지의 명칭을 builder로 지정한다
FROM golang:1.18 AS builder
WORKDIR /app 
COPY go.mod ./
COPY go.sum ./
RUN go mod download
COPY . ./
RUN go build -o simple-app .

# 두 번째 빌드 스테이지 (실행 단계)
FROM alpine:latest

# 최종 실행 파일만 복사
COPY --from=builder /app/simple-app /usr/loca/bin/simple-app

# 기본 실행 명령어 설정
CMD ["simple-app"]
```

### multi-stage build의 장점

#### 경량 이미지 생성

불필요한 빌드 도구나 라이브러리를 최종 이미지에 포함하지 않기 때문에 결과적으로 이미지 크기가 작아진다

#### 성능 향상

각 스테이지는 별도의 캐시를 가지며 빌드 중에 명령어에 대한 레이어 캐시를 찾는다

만약 캐시 무효화가 발생하면 그 이후의 명령어가 모두 실행되지만 그 범위는 해당 스테이지로만 국한되고 다음 스테이지에서는 해당 스테이지의 캐시를 사용할 수 있다

도커 파일 스크립트를 최적화해서 작성할수록 캐시 재사용을 통해 빌드 단계 시간을 절약할 수 있다

#### 빌드 과정 단순화

빌드 단계를 의미있는 단계로 나눠서 하나의 도커 파일로 관리함으로써 복잡한 빌드 파이프라인을 단순화할 수 있다

또한 각 단계는 독립적이기 때문에 특정 단계만 수정하고 다시 빌드할 수 있다

#### 보안 강화

도커는 기본적으로 마지막 스테이지의 결과물을 최종 이미지로 사용하기 때문에 실행에 필요한 파일만 포함할 수 있다

따라서 잠재적으로 보안 취약점이 될 수 있는 빌드 도구, 라이브러리 또는 소스 코드가 이미지에 포함되지 않기 때문에 보안을 강화할 수 있다


## environment variables

컨테이너 동작과 설정을 동적으로 제어할 수 있는 키-값 쌍을 말한다

환경 변수는 두 가지 방법으로 설정할 수 있다

1. 이미지 빌드 시 도커 파일 내에서 정의
2. 컨테이너 실행 시 외부에서 주입

### 도커 파일 내에서 환경 변수 정의와 주의점

ENV 명령어를 사용하여 특정 환경 변수를 이미지에 영구적으로 포함시킬 수 있다

정의된 변수들은 이미지 빌드 이후 생성되는 모든 컨테이너에서 기본적으로 사용된다

또한 도커 파일내에서 이후의 모든 명령어에서도 사용할 수 있다

ARG 명령은 이와 달리 빌드 시점의 변수로 도커 파일 내에서만 사용할 수 있고 최종 이미지에는 포함되지 않는다

```dockerfile
ENV APP_ENV=production
```

ENV 명령어로 설정한 환경 변수는 이미지의 메타데이터에 포함되어 외부에서 확인할 수 있다 (docker inspect)

따라서 민감한 정보는 도퍼 파일의 ENV에 직접 포함시키는 것보다 외부에서 주입하는 방식을 사용하는 것이 보안상 더 안전하다

### 컨테이너 실행 시 환경 변수 설정

컨테이너를 실행할 때 `-e`, `--env` 또는 `--env-file` 옵션을 사용하여 환경 변수를 주입할 수 있다

도커 파일에 정의된 환경 변수를 덮어쓰거나 추가할 때 유용하다

```shell
docker run -e APP_ENV=staging my-app
```

위 명령어는 도커 파일 내에서 정의한 APP_ENV 환경 변수의 값을 staging으로 재정의한다

`--env-file` 옵션을 사용하면 여러 환경 변수를 포함하는 파일을 지정할 수 있다

```shell
docker run --env-file ./env.list my-app
```

`--env-file` 옵션에 사용되는 파일은 일반적으로 텍스트 파일이며 각 줄마다 하나의 환경 변수를 `key=value` 형식으로 작성한다

키와 값 사이에 공백을 두지 않는 것이 문제를 발생시키지 않으며 주석 처리된 `#` 줄과 빈 줄은 무시된다

값에 공백이나 특수문자가 포함될 경우 큰따옴표나 작은따옴표로 감싸서 작성할 수 있다 

```text
APP_ENV=development
APP_PORT=8080
APP_NAME="example application"
```

### 유연한 동작 


도커 이미지는 설정값의 기본값을 포함해 패키징되지만 컨테이너를 실행할 때 이 설정값을 바꿀 수 있어야 함

도커 컨테이너의 환경변수를 이용하면 간단하게 설정값을 바꿔 애플리케이션의 동작을 의도한대로 변경할 수 있음

동일한 이미지를 가지고도 설정값에 의해 동작을 다르게 할 수도 있음

즉, 애플리케이션의 설정값을 컨테이너에서 받도록하여 다른 환경에도 애플리케이션을 배포할 수 있도록 이식성 있는 이미지를 만들 수 있어야 함


## image name (image reference)

도커 이미지 이름(이미지 참조)은 도커 레지스트리에서 이미지를 식별하고 접근하기 위한 고유한 문자열이다

단순 이름뿐만 아니라 이미지의 버전(태그)나 고유 식별자(다이제스트)를 포함할 수 있다

이미지 이름의 기본 형식

```text
[registry_host [:port]/] repository_name [:tag] [@digest]
```

`registry_host [:port]` optional
- `myregistry.example.com:5000
- 이미지가 저장된 도커 레지스트리의 주소
- 생략하면 도커 허브가 선택된다

repository_name
- 도커 이미지의 저장소 이름
- 일반적으로 username/repository 형식을 사용하여 도커 레지스트리의 사용자나 조직에서 관리하는 이미지를 표시한다
- `ubuntu:20.04`: 도커 허브에서 `library/ubuntu` 저장소의 20.04 태그를 가진 이미지를 의미한다 
- `nginx`: 태그가 생략되었으므로 `nginx/latest`로 해석된다

`:tag` optional
- 이미지의 버전을 지정하는 태그
- 태그를 지정하지 않으면 latest가 사용된다
- `ubuntu:20.04` `nginx:latest`

`@digest` optional
- 이미지의 고유 식별자인 다이제스트(SHA256)를 사용하여 특정 이미지를 참조한다
- `ubuntu@sha25:asdf1234...`
- 다이제스트는 이미지의 내용이 변경되지 않는 한 일정하기 때문에 이미지의 불변성을 보장할 때 유용하다


## docker file

도커 엔진은 도커 파일을 해석하여 명령어를 실행하고 최종 결과물을 이미지로 빌드한다

도커 파일은 컨테이너 이미지를 정의하는 명령어 집합으로 각 명령어는 이미지와 관련된 설정 작업(베이스 이미지, 파일 시스템 구성, 환경 변수, 실행 명령 지정 등)을 수행한다

### docker file path and build context

이미지를 빌드하기 위해 도커 데몬이 접근할 수 있는 빌드 컨텍스트(파일/디렉토리 범위)를 지정해야 한다

기본적으로 도커 엔진은 도커 파일을 빌드 컨텍스트 바로 아래에서 찾으며, 도커 파일은 컨텍스트에 포함된 컨텐츠만 접근할 수 있다

빌드 컨텍스트 외부에 도커 파일이 존재하는 경우 -f 옵션으로 도커 파일의 경로를 지정할 수 있다

컨텍스트 크기가 클수록 빌드 속도가 느려지기 때문에 적절한 설정이 필요하며 컨텍스트 외부 파일은 도커 파일에서 사용할 수 없다는 것을 유의해야 한다

```shell
docker build .

# 현재 디렉토리를 빌드 컨텍스트로 설정한다
# Dockerfile을 현재 디렉토리에서 찾는다 ./Dockerfile
```

```shell
docker build -f ./prod ./myapp

# Dockerfile을 현재 디렉토리의 prod 디렉토리 하위에서 찾는다 ./prod/Dockerfile
# ./myapp 디렉토리를 빌드 컨텍스트로 지정한다 
```

### docker file workflow

#### 1. load build context

도커 엔진은 빌드 컨텍스트로 지정한 디렉토리를 확인한다

#### 2. parse & run docker file

도커 파일의 명령어를 한 줄씩 읽어 명령어를 실행한다

#### 3. create & cache image layer

각 명령어의 실행 결과에 따른 상태 변화(파일 시스템 변화, 환경 변수, 패키지 설치 등)를 기반으로 이미지를 생성한다

도커는 명령어 (명령어 자체)를 기반으로 해시값을 생성하고 이 값을 기반으로 이미지를 캐시한다

#### 4. use cache or cache busting

각 명령어에 대한 해시값과 이전에 빌드한 해시값을 비교한 뒤 일치하면 기존에 캐시한 이미지 레이어를 재사용하여 빌드를 최적화한다

만약 해시값이 서로 다르다면 해당 명령어부터 이후의 명령어는 캐시 무효화(cache busting)이 발생하여 모두 재실행되고 다시 이미지를 생성하여 캐시한다

#### 5. complete read-only image

모든 명령어 실행이 완료되면 읽기 전용 이미지를 저장한다

이후 이미지를 실행하여 컨테이너를 생성 및 실행하고 최상단에 읽기-쓰기 가능 레이어(writable layer)를 추가한다 



## example for docker file

```dockerfile
# base image layer
FROM ubuntu:20.04 

# intermediate layer: 명령어 실행
RUN apt-get update
RUN apt-get install -y vim
COPY . /app

# metadata layer: 컨테이너가 실행될 때 기본적으로 실행할 명령 설정
CMD ["vim"]
```

### build docker file (build docker image)

```shell
docker build -t example-image .
# -t example-image: example-image라는 이름으로 이미지를 태그한다
# .: 도커 파일이 위치한 현재 디렉토리를 빌드 컨텍스트로 사용한다 (도커 파일은 빌드 컨텍스트인 현재 디렉토리 아래에서 찾는다)
```

위 명령어를 실행하여 이미지를 빌드하면 example-image라는 이름으로 도커 이미지가 생성된다 

`docker images` 명령으로 생성된 이미지를 확인할 수 있다


## docker file commands

기본 이미지 및 메타데이터 설정: [FROM](#from), [LABEL](#label), [ARG](#arg)

환경 설정 및 작업 디렉토리 설정: [ENV](#env), [WORKDIR](#workdir), [USER](#user), [EXPOSE](#expose)

파일 시스템 구성 및 데이터 추가: [COPY](#copy), [ADD](#add), [VOLUME](#volume)

빌드 시 명령 실행 및 컨테이너 실행 시 실행 명령 지정: [RUN](#run), [CMD](#cmd), [ENTRYPOINT](#entrypoint)

기타: [HEALTHCHECK](#healthcheck) [SHEEL](#shell)

### FROM

도커 파일의 첫 번째 명령어로서 빌드할 이미지의 베이스 이미지를 지정한다

도커 파일 내에서 반드시 한 번 이상 사용해야 하며 [멀티 스테이지 빌드](#multi-stage-build)에서는 여러 번 사용되어 각 스테이지를 구분한다

운영체제가 아닌 베이스 이미지는 보통 언어 실행에 필요한 런타임 환경을 제공한다

```dockerfile
FROM ubuntu:20.04

FROM openjdk:21-jdk-slim
```

### LABEL

이미지에 메타데이터(버전, 작성자, 설명 등)를 추가한다

이미지 검색, 관리/유지보수에 필요한 정보를 담을 수 있다

```dockerfile
LABEL maintainer="example@example.com" \
      version="1.0" \
      description="example application"
```

### ARG

빌드 시점(build-time) 변수로서 도커 파일 내에서 사용할 수 있는 변수 값을 정의한다

빌드 시점에 값을 주입할 수 있으며 기본값을 지정할 수 있다

ENV 명령과 달리 도커 파일 내에서만 사용할 수 있고 최종 이미지에는 포함되지 않기 때문에 컨테이너 실행 시에는 영향을 주지 않는다

```dockerfile
ARG APP_VERSION=1.0.0

RUN echo "build version $APP_VERSION"
```

### ENV

컨테이너 내에서 사용할 환경 변수를 설정한다

`key=value` 형식으로 값을 지정하며 설정된 변수는 도커 이미지 내에 포함되어 컨테이너 실행 시 접근할 수 있다

ENV 명령어로 설정한 환경 변수는 이미지의 메타데이터에 포함되어 외부에서 확인할 수 있다 (docker inspect)

따라서 민감한 정보는 도퍼 파일의 ENV에 직접 포함시키는 것보다 외부에서 주입하는 방식을 사용하는 것이 보안상 더 안전하다

```Dockerfile
ENV APP_ENV=production \
    APP_PORT=8080
```


### WORKDIR

명령어 실행 결과를 담을 기본 작업 디렉토리를 설정한다

여러 번 사용할 경우 각 단계마다 상대 경로가 기준되므로 계층적인 디렉토리 구조를 가진다

리눅스와 윈도우 컨테이너 모두 구분자로 슬래시(/)를 사용한다

```dockerfile
# 작업 디렉토리는 /app/service가 된다
WORKDIR /app
WORKDIR service

# 작업 디렉토리는 /service가 된다 (/app에서 /service로 완전히 재설정)
WORKDIR /app
WORKDIR /service
```

### USER

컨테이너 내에서 명령어를 실행할 때 사용자나 그룹을 지정한다

루트 사용자 대신 일반 사용자로 실행하도록 설정할 수 있다

```dockerfile
UESR myuser
```

### EXPOSE

컨테이너가 사용할 포트의 정보를 제공한다

실제 포트 매핑은 `docker run -p` 옵션으로 이뤄지지만 도커 파일 내에서 명시적으로 알릴 수 있다

```dockerfile
EXPOSE 80 443
```

### COPY

빌드 컨텍스트 내의 파일 또는 디렉토리를 컨테이너 파일 시스템으로 복사한다

단순 복사만 수행하며 압축 해제 등의 추가 기능을 제공하지 않는다

기본적으로 파일 권한과 소유권도 그대로 복사된다

```dockerfile
COPY ./src /app/src
```

### ADD

COPY와 유사하지만 URL에서 파일을 가져오거나 압축된 파일(tar.gz 등)을 자동으로 압축해제 하는 기능이 있다

자동 압축 해제 기능이 필요할 때 유용하며 단순 복사에는 COPY를 권장한다

```dockerfile
ADD https://example.com/file.tar.gz /app/
```

### VOLUME

컨테이너 내의 특정 경로를 데이터 볼륨으로 선언하여 데이터 지속성(persistence) 및 컨테이너 간 데이터 공유를 가능하게 한다

선언된 볼륨을 컨테이너가 종료/삭제되더라도 데이터를 유지할 수 있도록 도와준다

주로 데이터베이스나 로그 파일 저장용으로 사용한다

```dockerfile
VOLUME ["/data"]
```

### RUN

이미지 빌드 과정 중 명령어를 실행하여 파일 시스템 변경 작업을 수행하고 그 결과를 새로운 이미지 레이어로 만든다

주로 패키지 설치, 파일 시스템 구성, 빌드 도구 실행 등에 사용되며 실행 결과(파일 생성, 수정 등)가 이미지에 반영되어 [레이어 캐싱](#image-build--caching-mechanism)에 활용된다

```dockerfile
RUN apt-get update && apt-get install -y git
```

### CMD

컨테이너가 실행될 때 기본적으로 실행할 명령어나 인자를 지정한다

단 한번만 사용할 수 있으며 컨테이너 실행 시 `docker run` 명령의 인자로 대체할 수 있다

보통 ENTRYPOINT와 함께 사용되어 기본 명령에 인자를 추가하는 방식으로 사용된다

```dockerfile
`CMD ["python", "app.py"]
```

### ENTRYPOINT

컨테이너 실행 시 항상 실행될 기본 실행 파일이나 명령어를 지정한다

CMD와 함께 사용하면 ENTRYPOINT에서 지정한 명령어에 기본 인자 또는 추가 인자를 전달할 수 있다

컨테이너 실행 시 커맨드라인에서 인자를 제공하면 이를 덮어쓴다

```dockerfile
ENTRYPOINT ["nginx", "-g", "daemon off;"]
```

```dockerfile
ENTRYPOINT ["python", "-m", "http.server"]
CMD ["8000"]
```

### HEALTHCHECK

컨테이너의 상태를 주기적으로 점검할 명령어를 정의한다

컨테이너가 정상적으로 실행 중인지 모니터링하며 이상이 감지되면 재시작 등의 조치를 취할 수 있도록 도와준다 

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost/ || exit 1
```

### SHELL

기본적으로 리눅스 컨테이너의 경우 /bin/sh가 사용되지만 필요에 따라 쉘을 지정할 수 있다

```dockerfile
SHELL ["/bin/bash", "-c"]
```