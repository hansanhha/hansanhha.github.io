---
layout: default
title:
---

#### index
- [prometheus.yml](#prometheusyml)
  - [global](#global)
  - [scrape_configs](#scrape_configs)
  - [docker_sd_configs](#docker_sd_configs)
  - [recording rules](#recording-rules)
  - [alerting rules](#alerting-rules)
- [docker config](#docker-config)
- [docker compose config](#docker-compose-config)


## prometheus.yml

[prometheus configuration docs](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#configuration)

promethueus.yml는 프로메테우스의 설정 파일로 프로메테우스 서버의 스크래핑 설정, 메트릭 수집 대상/수집 주기 , 알림 설정, 시계열 데이터 저장소 설정, 라벨링 및 데이터 필터링 등을 정의한다

이 파일은 yaml 포맷으로 작성되며 프로메테우스가 시작될 때 필수적으로 필요하다

```yaml
global: # 프로메테우스의 기본 동작을 설정한다

scrape_configs: # 메트릭을 수집할 대상을 정의한다

scrape_config_files: # 로드할 외부 스크래핑 설정 파일을 지정한다

rule_files: # 알림 규칙을 정의하는 파일을 지정한다

alerting: # alertmanager와의 연동 설정을 정의한다

remote_read: # 프로메테우스가 외부 원격 저장소에서 데이터를 읽어올 때의 설정을 정의한다

remote_write: # 프로메테우스가 외부 원격 저장소에서 데이터를 전송할 때의 설정을 정의한다

storage: # 프로메테우스의 시계열 데이터 저장소 설정을 정의한다  
```

### global

프로메테우스의 전역 설정을 담당하는 부분

```yaml
global:
  
  # 메트릭을 수집하는 주기를 정의한다 (기본값: 1m)
  scrape_interval: 15s
  
  # 스크랩 요청 타임아웃을 설정한다 (기본값: 10s)
  # scrape_interval의 값보다 큰 값을 지정할 수 없다
  scrape_timeout: 10s

  # 규칙 평가 주기를 설정한다 (기본값: 1m)
  evaluation_interval: 1m

  # 외부 시스템(federation, remote storage, alertmanager 등)에서 사용되는 시계열이나 알림에 부여할 라벨을 설정한다
  # ${var} 또는 $var를 사용하면 현재 환경 변수로 치환된다
  external_labels:
#    [ <label name>: <label value> ...]
    system: ${system}
```

### scrape_configs

스크랩을 할 대상과 동작을 정의하며 일반적으로 하나의 스크래핑 구성은 하나의 job을 지정한다

스크랩 대상은 `static-configs` 매개변수를 통해 정적으로 지정하거나 서비스 디스커버리 메커니즘을 사용하여 동적으로 지정될 수 있다

또한 `relabel_configs`를 사용하면 스크래핑하기 전에 모든 대상과 해당 라벨에 대한 추가적인 수정 작업을 거칠 수 있다

```yaml
scrape_configs:
  
  -
    
    # 각 스크랩 작업의 이름을 지정하며, 스크랩된 메트릭에 기본적으로 할당된다
    job_name: example-monitor
  
    # 고정된 수집 대상을 설정한다
    static-configs:
      - targets: ['localhost:8080']

    # 수집할 메트릭 엔드포인트를 지정한다 (기본값: /metrics)
    metrics_path: /metrics  

    # 라벨 변환/필터링을 설정한다
    # 아래의 설정은 __address__ 라는 내장 라벨을 instance라는 새로운 라벨로 변환한다
    relabel_configs:
      - source_labels: [__address__]
        target_label: instance

    # 해당 job에서 스크랩 주기를 설정한다 (기본값: global.scrape_interval)
    scrape_interval: 15s

    # 해당 job의 스크랩 타임아웃을 설정한다 (기본값: global.scrape_timeout)
    # 마찬가지로 scrape_interval 보다 큰 값을 지정할 수 없다
    scrape_timeout: 10s
```

### docker_sd_configs

[prometheus docker_sd_configs docs](https://prometheus.io/docs/prometheus/latest/configuration/configuration/#docker_sd_config)

scrape_configs 하위에서 지정할 수 있는 옵션으로 docker service discovery를 설정하여 프로메테우스가 도커 엔진으로부터 스크랩할 도커 컨테이너를 검색하고 컨테이너의 메트릭을 수집할 수 있게 한다

도커에서 실행되는 애플리케이션의 메트릭을 수집할 때 유용하며 도커의 컨테이너가 추가되거나 삭제될 때마다 프로메테우스가 자동으로 검색한다

```yaml
scrape_configs:

  docker_sd_configs:
    
    -

      # 프로메테우스가 도커 api에 접근할 때 사용할 도커 호스트를 설정한다
      # 일반적으로 unit socket을 사용하거나 tcp 프로토콜을 통해 설정할 수 있다
      # 아래의 설정을 통해 프로메테우스는 도커의 unit 소켓을 통해 컨테이너 정보를 검색한다
      host: 'unix:///var/run/docker.sock'

      # 도커 컨테이너를 필터링하기 위한 조건을 설정한다
      # 특정 라벨이나 컨테이너 이름 등을 기준으로 필터링할 수 있다
      # 아래의 설정은 app=myapp 이라는 라벨을 가진 컨테이너만 검색하도록 조건을 지정한다
      filters: 
        - labels: "app=myapp"
```

## recording rules

프로메테우스는 recording rule과 alerting rule을 정의할 수 있도록 지원하는데, 그 중 recording rule은 자주 사용하는 쿼리나 복잡한 계산 로직을 미리 계산된 시계열 데이터로 저장하는 데 사용된다

rule은 prometheus.yml 파일이 아닌 별도의 yaml 파일에 작성하고 rules_files 속성으로 해당 rule 파일을 로드하는 방식으로 적용된다 

#### 주의점

recording rules는 새로운 시계열을 생성하므로 프로메테우스의 저장소 사용량이 늘어날 수 있기 때문에 너무 많은 규칙을 생성하지 않도록 한다

interval를 짧게 설정하면 프로메테우스 서버의 부하를 증가시킬 수 있다

너무 많은 라벨을 사용하면 cardinality가 급격히 증가하여 메모리 사용량에 영향을 미친다

```yaml
# rules/default-rule.yml

# groups 라는 최상위 키워드로 시작하며 각 rule을 그룹화하는 역할을 한다
groups:
  -
    
    # 그룹의 이름을 정의한다
    name: example-rules
    
    # 이 그룹의 규칙이 평가되는 주기를 설정한다
    # 기본값: global.evaluation_interval
    interval: 30s
    
    # 이 그룹에 포함될 recording rule 목록을 정의한다
    rules:
      -
        
        # 새롭게 생성될 메트릭 이름을 정의한다
        record: code:prometheus_http_requests_total:sum
        
        # promql 쿼리를 정의한다 (이 쿼리를 기반으로 시계열 데이터 생성)
        expr: sum by (code) (prometheus_http_request_total)

        # 생성된 메트릭에 추가할 라벨을 정의한다
        labels:
          job: 'node'
```

위의 rule 파일을 아래처럼 prometheus.yml 파일에서 참조하여 rule들을 적용시킬 수 있다

```yaml
# prometheus.yml

rules_files:
  - 'rules/default.yml'
```

### alerting rules

alerting rules는 프로메테우스에서 모니터링 데이터에 대한 조건을 정의하고 특정 조건이 충족될 때 알람을 트리거하도록 설정하는 규칙이다

정해진 조건을 지속적으로 평가하여 시스템 상태를 감시하고 설정된 조건에 달성하면 alertmanager와 통합하여 이메일, 슬랙 등 다양한 알림 채널을 통해 알림을 전송한다

recording rule과 함께 같은 파일에 정의할 수 있으며 prometheus.yml에서 이 파일을 로드하여 규칙이 적용되는 방식으로 동작한다

#### 주의점

짧은 순간의 스파이크(spike)나 일시적인 상태 변화로 불필요한 알람이 발생하지 않도록 for 옵션을 사용하여 일정 시간 동안 조건이 충족되었을 때만 알람을 발생시킨다

alertmanager의 라우팅 규칙을 쉽게 설정하도록 알람 라벨을 체계적으로 관리한다

주요 지표에만 중요도를 부여하고 정말 필요한 경우에만 알람을 발생하도록 설정하여 경로 피로(alert fatigue)를 유발하지 않도록 한다

설정한 alerting rules를 미리 테스트하여 의도한 대로 작동하는지 검증한다

```yaml
# recording rules와 동일한 구조로 rule을 정의한다
groups:
    name: example-rules
    interval: 30s
    rules:

      -
        
        # 알람의 이름을 설정한다
        alert: high_cpu_usage
        
        # promql 쿼리를 사용하여 알람을 발생시킬 조건을 정의한다
        expr: avg(rate(note_cpu_seconds_total[5m])) by (instance) > 0.8
        
        # 조건이 일정 시간 지속되었을 때만 알람을 발생시킨다
        for: 2m
        labels:
          severity: critical
          
        # 알람에 대한 추가 설명을 제공한다
        annotations:
          summary: 'high cpu usage detected on {{ $labels.instance }}'
          description: 'the cpu suage is above 80% for more than 2 minutes.'
```


## docker config

### 1. 도커 이미지 pull

```shell
docker pull prom/prometheus 
```

### 2. 프로메테우스 도커 네트워크 생성

```shell
docker network create prometheus-network
```

### 3. prometheus.yml 작성

```yaml
global:
  scrape_interval: 15s

  external_labels:
    monitor: 'prometheus-example-monitor'

scrape_configs:
  - job_name: 'prometheus'

    scrape_interval: 5s

    static_configs:
      - targets: ['localhost:9090']
```

###  4. 프로메테우스 도커 컨테이너 실행

```shell
docker run -d --name prometheus --network prometheus-network -p 9090:9090 \
-v ./prometheus.yml:/etc/prometheus/prometheus.yml \
prom/prometheus
```

## docker compose config

### 1. docker-compose.yml 파일 확인

```yaml
services:

  prometheus:
    image: prom/prometheus
    volumes:
      - prometheus-vol:/prometheus
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - '9090:9090'
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--storage.tsdb.retention.time=30d'
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/console'

volumes:
  prometheus-vol:
    external: true
```

**volumes** 옵션은 데이터를 컨테이너의 데이터를 영속화하기 위해 사용되지만 호스트 시스템과 컨테이너 간에 파일 공유를 위해서 사용될 수도 있다

`./prometheus.yml:/etc/prometheus/prometheus.yml`
- 호스트의 prometheus 설정 파일을 컨테이너 내의 프로메테우스 설정 파일과 직접 연결한다
- 이로써 호스트의 설정 파일을 수정하면 즉시 반영되어 컨테이너를 재배포하지 않아도 환경 설정을 변경할 수 있다 (컨테이너 재시작은 필요함)
- 또한 개발/운영 등의 환경 별로 설정 파일을 구분하여 환경에 맞게 설정 파일을 연결할 수도 있다

`prometheus-vol:/prometheus`
- `docker volume create prometheus-vol` 명령어로 미리 생성된 도커 볼륨을 컨테이너 내부의 `/prometheus` 경로에 마운트하여 프로메테우스의 데이터 영속화를 보장한다

`volumes.prometheus-vol.external=true`
- 도커 컴포즈가 외부에서 생성된 prometheus-vol 도커 볼륨을 참조하도록 설정한다

**command** 옵션은 도커 파일의 CMD를 덮어씌우는 역할을 하며 컨테이너 시작 시 실행할 명령어를 정의한다

`--config.file=/etc/prometheus/prometheus.yml`
- 프로메테우스가 사용할 설정 파일의 경로를 지정한다
- 위의 볼륨(volumes) 마운트 설정을 통해 호스트의 설정 파일을 컨테이너에 주입한다

`--storage.tsdb.path=/prometheus`
- 시계열 데이터(time series data)의 저장소 경로를 컨테이너 내부의 `/prometheus` 디렉토리로 지정한다
- 프로메테우스는 자체 시계열 데이터베이스(tsdb, time series database)를 사용하여 데이터를 디스크에 저장한다
- 데이터를 영속화하려면 위의 volumes 옵션에 지정한 것처럼 도커 볼륨을 `/prometheus` 디렉토리에 마운트해주면 된다

`- '--storage.tsdb.retention.time=30d'`
- 시계열 데이터를 30일간만 유지한다

`--web.console.libraries=/usr/share/prometheus/console_libraries`
- 프로메테우스의 웹 ui에서 사용할 콘솔 라이브러리 파일의 경로를 지정한다
- 주로 정적 html 템플릿 및 시각화 라이브러리를 포함한다

`--web.console.templates=/usr/share/prometheus/console`
- 프로메테우스 웹 ui에서 사용할 콘솔 템플릿 파일 경로를 지정한다


### 2. docker volume 생성

```shell
docker volume create prometheus-vol
```

### 3. prometheus.yml 파일 작성

[docker compose config의 파일](#3-prometheusyml-작성)과 동일하다

### 4. 프로메테우스 도커 컴포즈 실행

```shell
docker compose up -d prometheus
```





