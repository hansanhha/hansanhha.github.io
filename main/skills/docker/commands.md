---
layout: default
title:
---

[common commands](#common-commands)

[image commands](#image-commands)

[container commands](#container-commands)

[volume commands](#volume-commands)

[network commands](#network-commands)

[docker compose commands](#docker-compose-commands)


## common commands

run: 이미지로부터 새 컨테이너 생성 및 실행

exec: 실행 중인 컨테이너에 명령어 실행

ps: 컨테이너 목록 조회

images: 이미지 목록 조회

build: 도커 파일로부터 이미지 빌드

push/pull: 도커 레지스트리로부터 이미지 업로드/다운로드

login/logout: 도커 레지스트리에 로그인/로그아웃

version: 도커 버전 정보 표시

info: 시스템 정보 표시


## image commands

### 이미지 검색 및 다운로드

`docker search <image name>`: docker registry(doker hub)에서 이미지 검색

`docker pull <image name>:<tag>`: 이미지 다운로드 (태그 생략 시 'latest' 사용)

### 이미지 관리

`docker images`: 다운로드된 이미지 목록 확인

`docker inspect <image id or image name>`: 이미지 상세 메타데이터 확인

`docker tag <old image>:<tag> <new image>:<tag>`: 이미지 태그 변경

`docker rmi <image id or image name>`: 특정 이미지 삭제

`docker rim $(docker images -q)`: 모든 이미지 삭제

`docker image prune -a`: 사용되지 않는 모든 이미지 삭제

### 이미지 빌드 및 푸시

`docker build -t <image name>:<tag> <build context path>`: build context path 경로에 있는 도커파일을 기반으로 이미지 빌드 (도커 파일은 해당 컨텍스트에만 접근할 수 있다)
- `docker build -t example-docker-image:latest .`: 현재 경로를 빌드 컨텍스트로 설정하여 이미지를 빌드한다 (도커 파일은 현재 디렉토리에 접근할 수 있다)


`docker push <image name>:<tag>`: 이미지를 도커 레지스트리(docker hub)에 업로드


## container commands

### 컨테이너 실행

`docker run <image name>`: 새 컨테이너 실행

`docker restart <container id>`: 컨테이너 재시작

`docker start <container id>`: 실행 중지된 컨테이너 다시 시작

#### docker run 옵션

`-d (--detach)`: 백그라운드에서 컨테이너 실행 `docker run -d my-app`

`-p (--port)`: 컨테이너 포트와 외부(호스트) 포트 매핑 `docker run -p 8080:8080 my-app`

`-i (--interactive)`: 컨테이너의 표준 입력을 유지하여 상호작용할 수 있도록 설정 `docker run -it my-app /bin/bash`

`-t (--tty)`: tty(터미널) 할당 -> 인터랙티브한 셸 환경 제공 `docker run -it my-app /bin/bash`

`--rm`: 컨테이너 종료 시 자동 삭제 `docker run --rm my-app`

`--name`: 컨테이너 이름 지정 `docker run --name my-app my-app`

`-v (--volume)`: 호스트의 특정 디렉토리를 컨테이너 내부에 마운트 `docker run -v /mydata:/app/data my-app` (/mydata를 컨테이너의 /app/data로 마운트)

`--network`: 특정 네트워크에 컨테이너 연결 `docker run --network test-network my-app`

`-e (--env)`: 컨테이너 내 환경 변수 설정 `docker run -e "TEST_ENV=Hello my-app`

`--env-file` : .env 파일을 사용하여 여러 환경 변수 설정 `docker run --env-file /app/data/.env` (/app/data 하위의 .env 파일 사용)

`--hostname`: 컨테이너 호스트 네임 설정 `docker run --hostname test-hostname my-app`

`--tmpfs`: 컨테이너 내 특정 디렉토리를 tmpfs(메모리)로 마운트 `docker run --tmpfs /app/data my-app` (컨테이너의 /app/data 디렉토리를 메모리로 마운트)

##### 재시작 정책 (--restart)

컨테이너가 중지될 대 자동 재시작 여부를 결정하는 옵션

- `-no`: 기본값, 컨테이너 중지 후 자동 재시작 안함
- `on-failure`: 비정상 종료 시에만 재시작
- `alwasy`: 항상 재시작
- `unless-stopped`: 수동으로 중지하지 않는 한 재시작

`docker run --restart=always my-app`: 컨테이너가 항상 재시작하도록 설정

##### 리소스 제한

- `cpus`: 사용할 cpu 개수 제한
- `memory`: 최대 메모리 크기 제한
- `--memory-swap`: 스왑 메모리 제한(컨테이너 메모리 한도 초과 시 사용)
- `--cpu-shares`: cpu 점유율 가중치 설정(기본값: 1024)

`docker run --memory="512m" --cpus="1.5" my-app`: 최대 512MB 메모리와 1.5개 cpu 코어 사용 제한


### 컨테이너 확인

`docker ps`: 실행 중인 컨테이너 목록 조회

`docker ps -a`: 종료된 컨테이너까지 모든 컨테이너 조회

`docker logs <container id>`: 컨테이너 로그 출력

`docker logs -f <container id>`: 실시간 로그 확인 (follow)

`docker stats`: 실행 중인 컨테이너 리소스 사용량 확인(cpu, 메모리 사용량 실시간 모니터링)

### 컨테이너 정지 및 삭제

`docker stop <container id>`: 실행 중인 컨테이너 중지

`docker rm <container id>`: 실행 중지된 컨테이너 삭제

`docker rm -f <container id>`: 실행 중인 컨테이너 강제 삭제

`docker rm $(docker ps -aq)`: 중지된 컨테이너 일괄 삭제

### 컨테이너 접속

`docker exec -it <container id> /bin/bash`: 실행 중인 컨테이너 내부로 접속

### 컨테이너 이름 변경

`docker rename <old name> <new name>`: 컨테이너 이름 변경


## volume commands

### 볼륨 생성 및 마운트

`docker volume create <volume name>`: 새 볼륨 생성

`docker run -v <volume name>:<container data path> <image name>`: 볼륨을 container data path 경로에 마운트하여 컨테이너 실행

### 볼륨 목록 및 삭제

`docker volume ls`: 볼륨 목록 확인

`docker volume rm <volume name>`: 특정 볼륨 삭제

`docker volume prune`: 사용되지 않는 볼륨 삭제


## network commands

### 네트워크 관리

`docker network ls`: 네트워크 목록 조회

`docker network create <network name>`: 새 네트크 생성

`docker network rm <network name>`: 네트워크 삭제

`docker network connect <network name> <container name>`: 컨테이너를 특정 네트워크에 연결

`docker network disconnect <network name> <container name>`: 컨테이너 네트워크 연결 해제


## docker compose commands

### 도커 컴포즈 실행

`docker-compose up -d`: docker-compose.yml을 기반으로 컨테이너 실행

`docker-compose down`: 모든 컨테이너 중지 및 네트워크 제거

`docker-compose logs -f`: 모든 컨테이너 로그 확인



