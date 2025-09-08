---
layout: default
title:
---

[docker build overview](#docker-build-overview)

[Buildx](#buildx)

[BuildKit](#buildkit)

[build context](#build-context)


## docker build overview

도커 빌드는 아래의 구성 요소를 통해 클라이언트-서버 아키텍처를 따른다

#### client: buildx

buildx는 도커 빌드의 클라이언트로 실행 중이거나 관리 중인 빌드에 대한 유저 인터페이스 역할을 수행한다     

#### server: buildkit

buildkit은 도커 빌드의 서버 또는 빌더로 빌드 실행을 담당한다

### docker build workflow

1. 사용자가 빌드를 요청(`docker build`)하면 buildx 클라이언트가 buildkit 서버에게 빌드 요청을 보낸다
2. buildkit은 빌드 명령어를 처리하고 빌드 단계를 실행한다
3. 빌드 처리 결과는 클라이언트에게 응답하거나 도커 허브같은 레지스트리에 업로드한다

buildx와 buildkit은 기본적으로 docker desktop 및 docker engine과 함께 모두 설치된다


## Buildx

buildx는 `docker build` 명령어를 래핑하고 있는 cli 도구로 사용자가 `docker build`를 호출하면 빌드 옵션을 해석하고 buildkit 백엔드에게 빌드 요청을 전송한다

이후 bulidkit이 처리하는 빌드 상태를 모니터링하여 터미널로 진행 상황을 출력한다

이외에도 buildkit을 생성하거나 관리하는 기능과 레지스트리의 이미지를 관리하는 기능, 여러 빌드를 동시적으로 실행하는 기능을 지원한다

### difference between `docker build` and `docker buildx build`

`docker build` 명령어는 `docker buildx build`에 대한 별칭이지만 두 명령어 간에는 미묘한 차이가 있다

buildx를 사용하면 빌드 클라이언트와 데몬(buildkit)이 분리할 수 있기 때문에 단일 클라이언트로 여러 개의 빌더를 사용할 수 있다 

docker build는 항상 도커 엔진과 함께 제공되는 기본 빌더를 사용하도록 설정되어 있다 (이전 버전의 docker cli 하위 호환성을 보장하기 위해)

반면 docker buildx build는 buildkit에게 빌드 요청을 보내기 전에 다른 빌더를 기본 빌더로 설정했는지 확인함으로써 사용자가 도커 엔진에서 제공하는 빌더 이외에 다른 빌더를 사용하도록 커스터마이징할 수 있다

docker build에서 커스텀 빌더를 사용하기 위한 방법은 두 가지가 있다

#### 1. 명시적인 빌더 지정

cli에서 `--builder` 플래그나 BUILDX_BUILDER 환경 변수를 설정하는 방법이다

```shell
  BUILDX_BUILDER=my_builder docker build .
  docker build --builder my_builder .
 
```

2. buildx를 기본 클라이언트로 사용하도록 설정

아래의 명령을 실행해서 모든 빌드 관련 명령이 buildx를 통해 라우팅되도록 docker cli configuration 파일을 수정한다

```shell
docker buildx install

# 변경 전으로 되돌아가기
docker buildx uninstall
```


## BuildKit

buildkit은 빌드를 실행하는 데몬 프로세스로 사용자가 `docker build` 호출을 하면 buildx가 빌드 명령어를 해석하여 buildkit에게 빌드 요청을 전송하는데, 이 때 포함되는 정보는 다음과 같다

빌드 요청 정보
- Dockerfile
- build arguments
- export options
- caching options

buildkit은 빌드 실행 중에 빌드 명령어를 처리하고 빌드 단계를 수행한다

레거시 빌더가 로컬 파일 시스템을 항상 복사하는 방식과 다르게 builtkit은 로컬 파일, 빌드 비밀 변수 등 클라이언트로부터 필요한 리소스가 있으면 buildx에게 리소스를 요청한다

리소스 요청 대상 (예시)
- local filesystem build context
- build secrets
- ssh sockets
- registry authentication tokens


## build context







