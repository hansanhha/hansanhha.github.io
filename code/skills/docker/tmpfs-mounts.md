---
layout: default
title:
---

[tmpfs mounts](#tmpfs-mounts)


## tmpfs mounts

리눅스에서 도커를 실행 중이라면 volume, bind mounts 외에도 tmpfs mounts(temporary filesystem mounts)를 사용하여 컨테이너의 writable layer 바깥에 데이터를 쓸 수 있다

다만 volume과 bind mounts가 디스크에 데이터를 영구적으로 저장하는 것과 달리 tmpfs mounts는 호스트의 메모리에만 데이터를 영속화한다

따라서 컨테이너가 중지되면 tmpfs mounts는 삭제되고 더이상 파일이 영속화되지 않는다

tmpfs mounts는 컨테이너 내부나 호스트에 데이터를 영속하고 싶지 않은 경우(보안 이슈나 성능 이슈 등)에 가장 최적의 선택지이다

#### 유의점

도커의 tmpfs mounts는 리눅스 커널의 tmpfs에 직접 마운트하는데, 이 때 임시 데이터는 스왑 파일에 기록되어 파일 시스템에 유지될 수 있다

[참고 공식 문서](https://docs.docker.com/engine/storage/tmpfs)


## mounting over existing data

이미 데이터(파일, 디렉토리)가 존재하는 컨테이너의 디렉토리에 tmpfs mounts를 마운트하는 경우 기존 데이터가 감춰진다

이런 경우 컨테이너를 마운트없이 재생성하는 것을 권장한다


## limitation of tmpfs mounts

volume과 bind mountds와 달리 tmpfs mounts는 컨테이너간 공유할 수 없다

리눅스에서 실행 중인 도커에서만 지원되는 기능이다


## use tmpfs mounts

`docker run` 명령어에 `--mount` 또는 `--tmpfs` 플래그를 사용하여 tmpfs mounts를 사용할 수 있다

[--mount 옵션](https://docs.docker.com/engine/storage/tmpfs/#options-for---mount)

[--tmpfs 옵션](https://docs.docker.com/engine/storage/tmpfs/#options-for---tmpfs)

도커 스웜에서는 `--mount` 플래그만 사용할 수 있다

```shell
docker run --mount type=tmpfs dst=<mount-path>
docker run --tmpfs <mount-path>
```

```shell
# --mount flag
docker run -d \
-it \
--name tmptest \
--mount type=tmpfs,destintation=/app \
nginx

# --tmpfs flag 
docker run -d \
-it \
--name tmptest \
--tmpfs /app \
nginx:latest
```
 


