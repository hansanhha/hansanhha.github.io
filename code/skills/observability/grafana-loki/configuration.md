---
layout: default
title:
---

#### index
- [configuration file common keys](#configuration-file-common-keys)
- [local configuration example](#local-configuration-example)


## configuration file common keys

[loki configuration parameters](https://grafana.com/docs/loki/latest/configure/)

#### 공통 설정
- common

#### 인증 및 네트워크 관련 옵션
- auth_enabled: loki api 인증 활성화 여부
- server: loki 서버 포트 및 네트워크 설정 (read/write api)

#### 컴포넌트 관련 옵션
- common.ring: consitent hash ring 설정 (Distributor)
- ingester: 로그 저장, 청크 관리 설정 (Ingester) 
- querier: 쿼리 실행 관련 설정 (Querier)
- frontend: 쿼리 부하 분산 및 최적화 설정 (Query Frontend)
- ruler: alerting rules 설정 (Ruler)
- compactor: 로그 데이터 압축 및 보관 정책 설정 (Compactor)

#### 스토리지 및 데이터 보관 관련 옵션
- storage_config: 로그 데이터를 저장하는 스토리지 설정
- schema_config: 데이터 저장 스키마 설정 (버전, 인덱스 기간 등)
- compactor: 로그 데이터 압축 및 보관 정책 설정
- table_manager: 테이블 관리 및 데이터 만료 정책 설정

#### 쿼리 관련 옵션
- querier: 쿼리 실행 관련 설정
- frontend: 쿼리 부하 분산 및 최적화 설정
- limits_config.max_query_parallelism: 병렬 쿼리 실행 개수 제한
- limits_config.max_streams_per_user: 사용자별 허용되는 최대 로그 스트림 개수

#### 리소스 제한 및 성능 조정 옵션
- limits_config: 리소스 제한 및 제어
- limits_config.ingestion_rate_db: 초당 최대 로그 수집량(MB) 설정
- limits_config.ingestion_old_samples: 오래된 로그 거부 여부
- limits_config.reject_old_samples_max_age: 허용 가능한 로그 최대 연령 설정
- compactor.retention_enabled: 로그 보관 정책 활성화 여부

#### 데이터 분산 및 복제 설정
- common.ring.kvstore: consitent hash ring 스토리지 설정
- common.replication_factor: 로그 데이터 복제 개수 설정
- ingester.lifecycler.ring.replication_factor: ingester 데이터 복제 개수


## local configuration example

```yaml
auth_enabled = false

server:
  http_listen_port: 3100

common:
  ring:   
    instance_addr: 127.0.0.1
    kvstore: 
      store: inmemory
  replication_factor: 1
  path_prefix: /tmp/loki

schema_config:
  configs:
    -
      from: 2025-03-02
      store: tsdb
      object_store: filesystem
      schema: v13
      index:
        prefix: index_
        period: 24h

storage_config:
  filesystem:
    directory: /tmp/loki/chunks
```

인증 및 네트워크 설정
- `auth_enabled: false`: loki api 인증 비활성화
- `server.http_listen_port: 3100`: loki api가 3100 포트를 사용하도록 설정

공통 설정
- `common.ring.instance_addr: 127.0.0.1`: 현재 loki 인스턴스의 ip
- `common.ring.kvstore.store: inmemory`: consitent hash ring을 위한 key-value 저장소를 메모리로 설정
- `common.replication_refactor: 1`: 복제 개수를 한 개로 설정 (단일 노드이므로)
- `common.path_prefix: /tmp/loki`: loki의 메타데이터 저장 경로

스키마 설정
- `schema_config.configs.from: 2025-03-02`: 지정 날짜 이후부터 적용될 설정
- `schema_config.configs_store: tsdb`: tsdb 방식으로 데이터 저장
- `schema_config.configs.object_store: filesystem`: 파일 시스템을 스토리지로 사용
- `schema_config.configs.schema: v13`: loki 스키마 버전 지정
- `schema_config.configs.index.prefix: index_`: 인덱스 파일 접두사 설정
- `schema_config.configs.index.period: 24h`: 인덱스를 하루 단위로 설정

스토리지 설정
- `storage_config.filesystem.directory: /tmp/loki/chunks`: 로그 데이터 저장 경로 설정