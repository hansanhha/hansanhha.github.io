[container filesystem architecture](#container-filesystem-architecture)

[container writable layer](#container-writable-layer)

[docker volumes](#docker-volumes)

[anonymous volumes](#anonymous-volumes)

[mounting a volume over existing data](#mounting-a-volume-over-existing-data)

[use a volume](#use-a-volume)

[use a volume with docker compose](#use-a-volume-with-docker-compose)

[copy-on-write (cow)](#copy-on-write-cow)

[bind mount](#bind-mount)


## container filesystem architecture

컨테이너는 여러 레이어(이미지 레이어, 쓰기 가능 레이어 등)를 모아 단일 가상 디스크로 구성된 파일 시스템(리눅스 커널 기반 union file system)을 가진다

유니온 파일 시스템을 통해 물리적 위치가 서로 다른 파일과 디렉토리(하나 이상의 볼륨 마운트와 바인드 마운트 등)를 단일 디스크를 사용하듯 접근할 수 있다

```text
-----------container---------
|                           |
| -----------------------   |
| |   writeable layer   |   | <--- 모든 컨테이너에는 쓰기 가능 레이어가 있다
| -----------------------   |       쓰기 가능 레이어는 컨테이너와 같은 생애주기를 가지므로 
|                           |       컨테이너가 삭제되면 쓰기 가능 레이어에 저장된 데이터도 함께 유실된다
| -----------------------   |
| |   local bind mount  |   | <--- 바인드 마운트는 호스트 컴퓨터 스토리지의 특정 경로를 컨테이너의 대상 디렉토리로 연결한다
| -----------------------   |   |  원본 스토리지는 호스트 컴퓨터에서 접근 가능한 로컬 디스크와 네트워크 스토리지 모두 가능
|                           |   |
| ------------------------- |   |
| |distributed bind mount | | <-|  
| ------------------------- |
|                           |
| -----------------------   |
| |     volume mount    |   | <--- 볼륨 마운트는 도커가 관리하는 객체인 볼륨과 컨테이너의 대상 디렉토리를 연결한다
| -----------------------   |
|                           |
| -----------------------   |
| |      image layer    |   | <--- 컨테이너의 초기 상태는 이미지 레이어에서 받은 상태로만 구성된다
| |  -----------------  |   |      도커파일 스크립트에서 이미지로 복사한 모든 파일이 해당된다
| |  |               |  |   |      읽기 전용이지만, 기록 중 복사(copy-on-write)를 사용해 컨테이너에서 수정할 수 있다
| |  -----------------  |   |
| |                     |   |
| |  -----------------  |   |
| |  |               |  |   |
| |  -----------------  |   |
| -----------------------   |
-----------------------------
```


image layer
- 초기 파일 시스템 구성
- 읽기 전용이며 여러 컨테이너가 공유한다
- 레이너는 적층 구조를 갖는데, 후속 레이어와 이전 레이어의 내용이 충돌하는 경우 후속 레이어의 내용이 적용된다

writable layer
- 단기 저장 (데이터 캐싱)
- 도커는 유니온 파일 시스템을 통해 데이터를 저장하는 방식이 성능 저하를 유발할 수 있으므로 대신 tmpfs mount 사용을 권장한다

tmpfs mount
- 컨테이너에서 데이터를 메모리에 저장하는 기능
- 고속 데이터 처리
- 디스크 i/o를 줄이거나 보안(디스크에 데이터를 남기지 않음)이 필요할 때 사용

volume mount
- 영구적인 데이터 저장
- 여러 컨테이너에서 볼륨을 사용할 수 있으며 데이터를 공유할 수 있다

local bind mount
- 호스트 컴퓨터-컨테이너 간 데이터 공유
- 소스 코드/설정 값을 개발자의 로컬 컴퓨터에서 컨테이너로 즉시 전달하고 싶을 때 사용

distributed bind mount
- 네트워크 스토리지 - 컨테이너 간 데이터 공유
- 가용성이 높지만, 로컬 디스크와 비교해 지원하지 않는 파일 시스템 기능이 있거나 성능 면에서 차이가 있다
- 읽기 전용으로 설정 파일을 전달하거나 공유 캐시로 활용 가능
- 또는 읽기 쓰기 가능으로 설정하여 데이터를 저장해 동일 네트워크 상의 모든 컨테이너나 컴퓨터와 데이터를 공유하는 데 적합하다


## container writable layer

도커 컨테이너를 실행하면 도커 이미지 레이어를 기반으로 최상단 레이어에 읽기-쓰기 가능 레이어(writable layer)가 추가된다

이미지 레이어는 모든 컨테이너가 공유하는 읽기 전용 레이어로 삭제할 때 까지 로컬 호스트 컴퓨터에 존재한다

쓰기 가능 레이어는 컨테이너 별 독립적으로 생기는 공간으로 컨테이너와 같은 생명주기를 가지므로 컨테이너가 삭제되면 포함된 모든 데이터도 함께 삭제된다

또한 writable layer는 파일 시스템을 관리하기 위해 리눅스 커널을 사용하여 union filesystem을 제공하는 스토리지 드라이버를 필요로 하는데, 이러한 추가적인 추상화는 성능을 감소시키는 원인이 된다   

그래서 도커는 컨테이너에 직접 데이터를 쓰는 방식보다 더 나은 3가지 방식을 제공한다

#### 1. volume 

컨테이너가 생성하고 사용하는 데이터를 영속화할 수 있는 도구로 완전히 도커에 의해 관리된다

#### 2. tmpfs mount

컨테이너가 일시적인 상태 데이터만 생성하는 경우 사용할 수 있는 방식

영구적으로 데이터를 저장하지 않으면서 컨테이너의 writable layer에 데이터를 쓰지 않아 성능을 높일 수 있다

#### 3. bind mount

컨테이너와 호스트의 파일과 디렉토리에 접근할 필요가 있을 때 사용할 수 있는 방식

도커에 의해 관리되지 않고 디렉토리 구조와 호스트의 os에 의존한다


## docker volumes

도커 볼륨은 컨테이너가 데이터를 영속화할 수 있는 기능으로 **도커에 의해 생성되고 관리된다**

`docker volume create` 명령어로 생성하거나 컨테이너 또는 서비스 생성 중에 도커 볼륨을 생성할 수 있다

생성된 볼륨은 도커 호스트의 디렉토리에 저장되며 볼륨을 컨테이너에 마운트하면, 디렉토리는 컨테이너에 마운트된다

-> 도커에 의해 관리되는 점과 호스트 시스템의 핵심 기능과 격리된다는 점을 제외하면 bind mount가 작동하는 방식과 유사하다 

볼륨은 rprivate (recursive private) 바인드 전파를 사용하며 볼륨에 대한 바인드 전파 방식을 구성할 수 없다

볼륨을 사용하면 아래와 같은 장점을 제공한다
- bind mount 방식보다 간단하게 백업 또는 마이그레이션을 할 수 있다
- docker cli 또는 docker api를 사용하여 볼륨을 관리할 수 있다
- 볼륨은 리눅스와 윈도우 컨테이너에서 모두 동작한다
- 여러 컨테이너에서 안전하게 공유되며 동시에 실행될 수 있다
- 컨테이너 또는 빌드에 의해 컨텐츠를 미리 채울 수 있다
- 고성능 i/o을 제공한다


## anonymous volumes

볼륨의 이름을 지정하지 않으면 도커에서 고유한 이름을 보장하는 랜덤 이름을 가진 익명 볼륨이 생성된다

익명 볼륨도 일반 볼륨과 마찬가지로 컨테이너가 삭제되도 데이터를 영속하지만 컨테이너를 생성할 때 `--rm` 옵션을 사용하면 컨테이너 삭제 시 함께 삭제된다

각각의 익명 볼륨을 사용하는 여러 컨테이너를 생성하는 경우, 각 컨테이너는 자신만의 볼륨을 생성하며 이 볼륨들은 재사용되거나 컨테이너간 데이터를 자동적으로 공유하지 않는다

두 개 이상의 컨테이너에서 익명 볼륨을 공유하려면 랜덤 볼륨 id를 사용하여 익명 볼륨을 명시적으로 마운트해야 한다


## mounting a volume over existing data

#### 파일 또는 디렉토리가 있는 상태의 컨테이너에 데이터가 존재하는 볼륨을 마운트하는 경우

볼륨의 데이터에 의해 컨테이너의 데이터가 가려진다 (obscured)

#### 파일 또는 디렉토리가 있는 상태의 컨테이너에 비어있는 볼륨을 마운트하는 경우

컨테이너의 데이터가 볼륨에 자동적으로 복사된다

이 방법을 이용하여 볼륨에 미리 컨텐츠를 채워넣을 수 있다




## use a volume
 
볼륨을 사용하는 방법은 간단하다

1. 볼륨 생성
2. 컨테이너와 볼륨 연결

### 볼륨 생성

#### 1. docker volume create 명령어 사용 (명시적 생성)

```shell
docker volume create my-vol
```   

#### 2. docker run의 -v 옵션 사용 (자동 생성)

컨테이너 실행 시 볼륨을 자동 생성하지만 만약 마운트하려는 볼륨이 이미 존재한다면 기존 볼륨을 그대로 생성한다

이름을 지정하지 않으면 도커는 [익명 볼륨](#anonymous-volumes)을 생성한다 

```shell
# my-vol 볼륨이 자동으로 생성되고(없는 경우) 컨테이너 내부의 /data 디렉토리에 마운트된다
docker run -v my-vol:/data my-app
```

#### 3. dockerfile에서 VOLUME 명령어 사용

해당 이미지를 실행하면 컨테이너를 실행할 때 항상 [익명 볼륨](#anonymous-volumes)이 자동으로 생성된다

```dockerfile
# 컨테이너 실행 시 익명 볼륨이 컨테이너 내부의 /data 디렉토리에 마운트된다 
VOLUME /data
```

### 컨테이너와 볼륨 연결

#### 1. docker run -v, --volume  옵션 사용 ()

myvol 이라는 볼륨 컨테이너의 /app 디렉토리에 마운트한다

컨테이너 내부의 /app에 저장된 데이터는 myvol 볼륨에 저장되며 컨테이너가 삭제되어도 데이터는 유지된다

```shell
docker run -d -v myvol:/app my-app
```

#### 2. docker run --mount 옵션 사용

-v 옵션보다 더 많은 flag(key=value 구성, 콤마로 구분)를 설정할 수 있는 옵션

type: 볼륨을 마운트하는 방식 지정

source: 마운트할 볼륨 이름

target: 컨테이너 내부에서 볼륨을 연결할 경로

```shell
docker run -d \
  --mount type=volume,source=myvol,target=/app \
  my-app
```

#### 3. 호스트 디렉토리를 마운트하기

도커에 의해 관리되는 볼륨 대신 호스트의 디렉토리에 직접 마운트할 필요가 있을 때 사용한다

아래의 명령어는 호스트 pc의 /home/user/data를 컨테이너 내부의 /app 디렉토리에 마운트한다

```shell
docker run -d -v /home/user/data:/app my-app

docker run -d \
  --mount type=bind,source=/home/user/data,target=/app \
  my-app
```


## use a volume with docker compose

아래와 같이 compose.yml 파일의 volumes 속성에 볼륨 이름을 명시하면 맨 처음 볼륨을 생성하고 그 이후 생성된 볼륨을 사용한다

```yaml
services:
  frontend:
    image: node:lts
    volumes:
      - myapp:/home/node/app
volumes:
  myapp:
```

또는 `docker volume create`로 직접 볼륨을 만들고 난 후 아래와 같이 compose.yml 파일에서 볼륨을 참조할 수 있다

```yaml
services:
  frontend:
    image: node:lts
    volumes:
      - myapp:/home/node/app
volumes:
  myapp:
    external: true
```

## copy-on-write (cow)

기본적으로 이미지는 모든 컨테이너에서 일관적인 상태로 공유할 수 있게 하기 위해 읽기 전용 레이어로 구성되어 있다

개별적인 컨테이너에서 이미지 레이어의 특정 파일을 수정하고자 할 때 copy-on-write 방식을 사용한다

이미지의 읽기 전용 레이어에 있는 파일을 컨테이너의 쓰기 가능한 레이어에 복사해서 수정한다


## bind mount

docker run --mount의 type 플래그를 bind로 설정하여 컨테이너에 바인드 마운트를 연결할 수 있다 

```shell
docker run --mount type=bind
```

마운트 또는 바인드 마운트는 호스트 컴퓨터 파일 시스템의 디렉토리를 컨테이너 파일 시스템의 디렉토리로 만드는 방식이다

컨테이너는 유니온 파일 시스템을 사용하므로 바인드 마운트나 볼륨이나 하나의 디렉토리에 불과하지만 컨테이너에서 호스트의 파일에 직접 접근할 수 있고 그 반대도 가능해진다

호스트 컴퓨터에서 접근 가능한 파일 시스템이라면 무엇이든 컨테이너에서도 사용할 수 있다
- SSD 디스크
- 네트워크 상에서 사용하는 분산 스토리지
- RAID가 적용된 디스크 어레이를 가진 서버 등

컨테이너는 호스트 컴퓨터에 대한 공격을 방지하기 위해 최소 권한을 가진 계정으로 실행되는데, 바인드 마운트를 사용하면 호스트 컴퓨터 파일에 접근하기 위해 권한 상승이 필요해진다

도커 스크립트에서 USER 명령어를 사용해 컨테이너에 관리자 권한을 부여할 수 있다 (리눅스-root, 윈도우-ContainerAdministrator)

## limitation of bind mount

### override a file with the same name

컨테이너가 마운트할 대상 디렉토리에 포함된 파일과 이미지 레이어에 명시된 파일명이 같은 경우 이미지 레이어에 명시된 파일이 덮어씌워진다

따라서 이미지에 포함된 원래 파일을 사용할 수 없게 된다

### differences in linux and windows containers work 

컨테이너에 이미 존재하는 디렉토리에 호스트의 파일 하나를 마운트하는 경우 리눅스 컨테이너는 기존 디렉토리에 하나의 파일이 정상적으로 마운트되는 반면, 윈도우 컨테이너는 에러를 발생시킨다

이외에도 컨테이너에 유형에 따른 바인드 마운트 동작 방식의 차이로 인해 에러가 발생할 수 있다

### lack of functionality in distributed file system

분산 파일 시스템을 컨테이너에 바인드 마운트한 경우 네트워크상의 모든 컴퓨터에서 데이터에 접근할 수 있지만, 분산 파일 시스템의 메커니즘은 로컬 컴퓨터 운영체제의 파일 시스템과 다른 경우가 많다 (AWS S3 등)

그래서 일반적인 파일 시스템 기능 중에서 지원하지 않는 기능이 있을 수 있다

컨테이너에 분산 스토리지를 마운트할 계획이라면, 바인드 마운트의 원본 스토리지가 컨테이너에서 사용하는 모든 파일 시스템 기능을 제공하지 않을 수 있기 때문에 로컬 스토리지와 차이가 있다는 것을 고려해야 된다