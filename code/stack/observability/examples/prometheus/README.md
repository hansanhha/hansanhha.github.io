#### index
- [run prometheus docker container](#run-prometheus-docker-container)
- [run prometheus with alertmanaager in docker compose](#run-prometheus-with-alertmanager-in-docker-compose)

## run prometheus docker container

#### 1. 도커 이미지 pull

```shell
docker pull prom/prometheus 
```

#### 2. 프로메테우스 도커 네트워크 생성

```shell
docker network create prometheus-network
```

#### 3. prometheus.yml 파일 확인

####  4. 프로메테우스 도커 컨테이너 실행 (이 디렉토리 경로에서 도커 명령어를 실행한다고 가정)

```shell
docker run -d --name prometheus --network prometheus-network -p 9090:9090 \
-v ./prometheus.yml:/etc/prometheus/prometheus.yml \
prom/prometheus
```

## run prometheus with alertmanager in docker compose

#### 1. 도커 이미지 pull

```shell
docker pull prom/prometheus 
``` 

#### 2. docker volume 생성

```shell
docker volume create prometheus-vol
```

#### 3. prometheus.yml 파일 확인

#### 4. docker-compose.yml 파일 확인

#### 5. alertmanager.yml 파일 확인

#### 5. 프로메테우스 도커 컴포즈 실행 (이 디렉토리 경로에서 도커 명령어를 실행한다고 가정)

```shell
docker compose up -d prometheus
```