---
layout: default
title:
---

#### index
- [jobs and instances](#jobs-and-instances)


## jobs and instances

job은 프로메테우스가 데이터를 수집해야 하는 대상 서비스나 작업의 논리적 그룹을 나타낸다

`web_server job`: 여러 대의 웹 서버 모니터링

`database job`: mysql, redis 같은 데이터베이스 모니터링

```yaml
scrape_configs:
  - job_name: 'web_server'
    scrape_interval: 5s
    static_configs:
      - targets: ['192.168.1.10:8080', '192.168.1.11:8080']
        labels:
          environment: 'production'
```

instance는 일반적으로 job에 속하는 하나의 단위로 특정 서비스나 애플리케이션의 개별 엔드포인트를 의미하며 스크랩한 호스트 ip + 포트 조합으로 각 instance를 식별한다

`192.168.1.10:8080`: 특정 서버의 메트릭을 수집하는 인스턴스

`app-server-1:8080`: 애플리케이션 서버 1번의 메트릭을 수집하는 인스턴스

up 메트릭은 프로메테우스가 해당 인스턴스에 정상적으로 접근할 수 있는지를 나타낸다

접근할 수 있으면 1을 나타내고, 스크랩을 실패하면 0을 나타낸다

```text
up{job="web_server", instance="192.168.1.10:9100"} 1
```





