---
layout: default
title:
---

[container networking basic concepts](#container-networking-basic-concepts)

[docker network driver type](#docker-network-driver-type)

[built-in networks](#built-in-networks)

[user-defined networks](#user-defined-networks)

[connect container to network](#connect-container-to-network)

[published ports](#published-ports)

[dns service](#dns-service)

[custom hosts](#custom-hosts)


## container networking basic concepts

컨테이너 네트워킹이란 컨테이너 간 서로 연결하거나 도커가 아닌 다른 호스트와의 통신을 의미한다

기본적으로 컨테이너는 네트워킹이 활성화되어 있고 다른 대상과의 연결(connection)을 만들 수 있다

다만 컨테이너는 ip 주소, 게이트웨이, 라우팅 테이블, dns 서비스 등 네트워크 상세 사항은 볼 수 있지만 연결된 네트워크 종류나 연결된 대상이 도커인지 아닌지에 대해서는 알 수 없다

### networking workflow

컨테이너는 자신의 네트워크 인터페이스에 할당된 ip 주소와 포트 번호를 사용하여 통신한다 (tcp/ip 스택 기반 네트워킹)

컨테이너 내부의 애플리케이션은 단순히 ip, 포트, dns 이름을 이용하여 다른 호스트와 통신하며 다른 호스트와 통신한다 (프로토콜 추상화)

다만 이 정보만으로는 상대방이 도커 컨테이너인지, 가상 머신인지, 물리적 서버인지 구분할 수 없다

또한 내부 dns 서비스를 제공하여 같은 네트워크 내의 컨테이너 이름이나 id를 통해 서로 통신할 수 있게 한다

### network namespace

도커는 리눅스 커널의 네임스페이스 기능을 사용하여 각 컨테이너가 독립된 네트워크 스택(ip 주소, 라우팅 테이블 등)을 갖도록 한다

컨테이너끼리 네트워크 격리를 제공하며 서로 다른 컨테이너가 동일한 ip 주소를 사용할 수 있도록 한다

### virtual ethernet(veth) pair

virtual ethernet pair는 호스트와 컨테이너, 컨테이너와 컨테이너 사이를 연결하기 위해 사용하는 가상 네트워크 인터페이스 쌍을 말한다

도커는 네트워크 트래픽이 컨테이너 내부와 외부로 원활하게 전달될 수 있도록 연결 채널(veth)을 제공한다

### routing and port mapping

도커는 라우팅 및 포트 매핑을 통해 컨테이너 간, 컨테이너와 외부 네트워크 간의 통신을 지원한다


## docker network driver type

도커는 서로 다른 격리 수준과 사용 사례를 기반으로 여러 종류의 네트워크 드라이버를 제공한다

드라이버는 네트워크 트래픽을 전달하며 연결된 대상의 내부 구현이나 실행 환경에 대해 알 필요가 없다

### bridge driver

컨테이너에 연결되는 기본 네트워크 드라이버로, 단일 호스트 내에서 컨테이너들이 통신할 수 있도록 가상 브릿지를 생성한다

포트 매핑을 통해 외부와 통신할 수 있다

### host driver

도커 호스트와 컨테이너 사이의 네트워크 격리를 제거하여 컨테이너가 호스트의 네트워크 스택을 직접 사용하는 드라이버

네임스페이스 격리가 없으므로 성능은 좋지만 격리성이 떨어진다

### overlay driver

여러 호스트에 분산된 컨테이너들을 연결할 수 있도록 지원하는 드라이버

docker swarm이나 k8s 같은 오케스트레이션 도구와 함께 사용되며 다중 호스트 클러스터 환경에서 유용하다

### ipvlan driver

ipv4와 ipv6 주소 할당에 대한 완전한 제어를 할 수 있는 드라이버

### macvlan driver

컨테이너에 물리적 네트워크 인터페이스 고유 mac 주소를 할당하는 드라이버

네트워크 상의 별도 장비처럼 보이게 한다

### none

네트워크 격리를 극대화하기 위해 컨테이너에 인터페이스를 제공하지 않는다


## built-in networks

도커를 설치하면 기본적으로 3가지 네트워크가 자동적으로 생성된다

이 네트워크들은 컨테이너를 실행할 때 네트워크 옵션을 지정하지 않으면 사용되거나, 필요에 따라 선택해서 사용할 수 있다

### bridge network

bridge 드라이버를 사용한 bridge 이름을 가진 도커의 기본 네트워크로 컨테이너를 실행할 때 네트워크 옵션을 지정하지 않으면 자동으로 할당된다

호스트의 `docker0` 브릿지를 통해 컨테이너들이 서로 통신할 수 있도록 한다

각 컨테이너는 자체 ip 주소를 할당받으며 nat(network address translation)를 통해 외부와 통신할 수 있다

### host network

컨테이너가 호스트의 네트워크 스택을 직접 공유하도록 하는 host 드라이버를 사용하는 네트워크다

`docker run --network host ...`와 같이 실행하여 host 네트워크를 사용할 수 있다

컨테이너는 자체 ip 주소를 할당받지 않고 호스트와 동일한 네트워크 인터페이스를 사용한다

포트 매핑이 필요없으며 컨테이너 내부의 서비스가 호스트 네트워크와 직접 연결된다

네트워크 격리를 하지 않아 성능이 좋지만 보안 측면에서 주의가 필요하다

### none network

컨테이너에 네트워크 인터페이스를 제공하지 않는 네트워크다

`docker run --network none ...` 옵션을 사용하여 적용할 수 있다

컨테이너는 외부 네트워크와 연결되지 않으며 기본적으로 루프백 인터페이스(loopback interface)만 존재한다

완전히 격리된 네트워크 환경이 필요하거나 네트워크 기능이 전혀 필요없는 작업에 사용된다


## user-defined networks

[도커가 제공하는 네트워크 드라이버](#docker-network-driver-type)을 기반으로 사용자가 네트워크를 생성할 수 있다

주로 다음과 같은 목적으로 네트워크를 정의한다
- 격리 및 보안: 네트워크를 별도로 분리하여 서로 다른 사용자 또는 서비스가 사용하는 컨테이너들을 논리적으로 분리한다
- 유연한 네트워크 구성: 서브넷, 게이트웨이, ip 주소 범위 등 네트워크 파라미터를 직접 설정한다
- 서비스 디스커버리: 도커 컴포즈나 오케스트레이션 도구와 함께 사용할 때 [도커가 제공하는 기본 네트워크](#built-in-networks) 대신 커스텀 네트워크를 통해 서비스 이름 기반의 연결을 할 수 있다

### create network

bridge(기본 드라이버) 네트워크를 생성하는 명령어는 다음과 같다

컨테이너가 동일한 네트워크에 연결되어 있으면 내장된 도커 dns 서버를 통해 컨테이너 이름 또는 id로 서로 통신할 수 있다

```shell
# 네트워크 드라이버, 네트워크 이름 지정
docker network create --driver bridge my-net

# 네트워크 드라이버, 서브넷, 게이트웨이 지정
docker network create \
  --driver bridge \
  --subnet 172.25.0.0/16 \
  --gateway 172.25.0.1 \
  my-custom-network
```

### inspect network

도커에 있는 네트워크 자체를 검사하려면 `docker network inspect <network name>` 명령을 사용하면 된다


## connect container to network

### --network option

컨테이너를 실행할 때 --network 옵션을 사용하여 네트워크에 연결할 수 있다

아래와 같이 my-net 네트워크에 연결된 컨테이너들은 서로 dns 이름으로 통신할 수 있다

```shell
docker run --network my-net --name=my-app   my-app
docker run --network my-net --name=my-app-2 my-app
```

### connect, disconnect

이미 실행 중인 컨테이너는 connect/disconnect 명령을 사용해서 네트워크를 연결/해제할 수 있다

```shell
# 실행 중인 my-app 컨테이너에 my-net 네트워크 연결
docker network connect my-net my-app

# 실행 중인 my-app 컨테이너에 my-net 네트워크 해제
docker network disconnect my-net my-app
```


## published ports

기본적으로 도커 컨테이너는 격리된 네트워크 내에서 실행되므로 외부에서 직접 접근할 수 없다

포트 매핑은 컨테이너의 포트를 호스트에 노출시켜 외부에서 컨테이너로 접근할 수 있도록 하는 메커니즘이다

컨테이너 내부의 네트워크 서비스(웹 서버, 데이터베이스 등)에 호스트의 ip 주소와 특정 포트를 통해 접근할 수 있다

도커 엔진은 nat(network address translation)를 사용하여 외부 요청이 호스트 ip 주소와 포트로 들어오면 컨테이너의 지정된 포트로 전달한다 (포트 포워딩)

매핑된 포트는 `docker port <container id or container name>` 명령어로 확인할 수 있다

### `-p`, `--publish`

브릿지 네트워크 외부의 서비스에서 포트를 사용하려면 -p 또는 --publish 플래그를 사용해야 한다

그러면 호스트에 방화병 규칙이 생성되고 컨테이너 포트와 도커 호스트 포트를 통해 외부와 매핑한다

```shell
# 호스트 아이피/포트, 컨테이너 포트/tcp 또는 udp를 지정할 수 있다 (생략 시 tcp 사용)
docker run -p [<host ip>]:<host port>:<container port>[/tcp or udp]
```

```shell
# 호스트의 80 포트를 컨테이너의 8080 tcp 포트에 매핑한다
docker run -p 80:8080 --name=my-app my-app

# 호스트의 192.168.1.100 ip 주소의 80 포트를 컨테이너의 8080 udp 포트에 매핑한다
docker run -p 192.168.1.100:80:8080/udp
```

여러 개의 포트를 매핑하려면 -p 옵션을 여러 번 사용하면 된다

```shell
docker run -d \
  -p 8080:80 \
  -p 8443:443 \
  --name=my-app my-app
```

### `-P`

-P 옵션은 컨테이너가 EXPOSE된 모든 포트를 호스트의 임의의 포트에 매핑한다

```dockerfile
EXPOSE 8080, 80
```

```shell
docker run -P --name=my-app my-app
```


## dns service

dns 서비스는 컨테이너 간의 이름 기반 통신과 서비스 디스커버리를 지원하는 역할을 한다

#### default bridge network and embedded dns server

기본적으로 컨테이너는 `/etc/resolv.conf` 파일에 정의된 대로 dns 설정을 상속한다

기본 bridge 네트워크에 연결된 컨테이너는 이 파일의 복사본을 받는다 

[커스텀 네트워크](#user-defined-networks)에 연결되는 컨테이너는 도커의 내장 dns 서버를 사용하여 외부 dns 조회를 호스트에 구성된 dns 서버로 전달한다

#### service discovery

동일한 사용자 정의 네트워크에 속한 컨테이너들은 서로의 이름이나 설정한 별칭(alias)을 통해 통신할 수 있다 

도커 컴포즈 같은 오케스트레이션 도구를 사용할 때 서비스 이름이 dns 이름으로 자동 등록되어 컨테이너들이 서로 쉽게 찾아 연결할 수 있다

#### load balancing

만약 여러 컨테이너가 동일한 이름을 갖고 있다면 도커 dns는 해당 이름에 대해 여러 ip 주소를 반환한다

클라이언트는 일반적으로 라운드 로빈 방식 등으로 이들 ip 중 하나에 연결하여 기본적인 로드 밸런싱 효과를 누릴 수 있다

### dns workflow

#### 1. request dns lookup

컨테이너 내의 애플리케이션이 dns 조회(gethostbyname() 호출 등)를 하면 먼저 컨테이너 내부의 `/etc/resolv.conf` 파일에 지정된 dns 서버로 요청이 전달된다

#### 2. internal dns lookup

내장 dns 서버는 해당 네트워크에 연결된 컨테이너들의 이름과 별칭 정보를 내부 데이터베이스(서비스 디스커버리 정보)를 통해 조회한다

조회하려는 호스트명이 동일 네트워크 내의 컨테이너 이름이나 별칭과 일치하면 해당 컨테이너의 ip 주소를 반환한다

여러 컨테이너가 같은 이름(서비스)으로 등록되어 있다면 여러 ip 주소를 반환하여 로드 밸런싱 효과를 제공한다

#### 3. external dns lookup

조회하려는 이름이 내부 컨테이너 이름과 일치하지 않는 경우 도커 dns 서버는 외부 dns 서버(`/etc/resolv.con`에 지정된 dns 서버)로 질의를 전달하여 결과를 받아온다


## custom hosts

커스텀 호스트는 컨테이너 내부의 `/etc/hosts` 파일에 사용자가 지정한 호스트 이름과 ip 주소 매핑을 추가하여 컨테이너 내에서 특정 도메인 이름을 원하는 ip 주소로 해석하도록 하는 기능이다

이를 통해 기본 dns 조회 결과를 보완하거나 테스트/개발 환경에서 임의의 호스트 이름을 설정할 수 있다

컨테이너가 시작될 때 도커는 기본적으로 컨테이너 이름, 로컬 루프백 주소(127.0.0.1) 등 기본 항목들이 포함된 컨테이너의 `/etc/hosts` 파일을 자동 생성한다

커스텀 호스트를 사용하면 이 파일에 추가적인 호스트 이름과 ip 주소 매핑이 삽입되어 컨테이너 내부에서 해당 이름을 지정된 ip로 해석할 수 있다

`/etc/hosts` 파일에 있는 항목은 일반적인 dns 조회보다 우선적으로 적용된다

### `--add-host`

컨테이너 실행 시 `--add-host` 옵션을 사용하여 커스텀 호스트를 추가할 수 있다

```shell
docker run --add-host <hostname>:<ip address> [options] <image>
```

```shell
docker run -d --name=my-app --add-host myapp.local:129.168.1.100 my-app
```

위 명령은 my-app 이라는 이름의 컨테이너를 실행하면서 컨테이너의 /etc/hosts 파일에 다음 항목을 추가한다

컨테이너 내부에서 `ping myapp.local` 혹은 `curl http://myapp.local`과 같이 접근하면 dns 조회 대신 해당 ip(192.168.1.100)로 연결된다

```text
192.168.1.100   myapp.local
```

### extra_hosts in docker-compose

도커 컴포즈 파일을 사용하는 경우 `extra_hosts` 옵션을 통해 커스텀 호스트를 지정할 수 있다

아래의 설정을 사용하면 web 서비스 컨테이너의 `/etc/hosts` 파일에 `192.168.1.100 myapp.local` 항목이 추가된다

```yaml
version: '3'
services:
  web:
    image: nginx
    extra_hosts:
      - "myapp.local:192.168.1.100"
```