---
layout: default
title:
---

#### index
- [alertmanager](#alertmanager)
- [alertmanager.yml](#alertmanageryml)
  - [global](#global)
  - [route](#route)
  - [receivers](#receivers)
  - [inhibit_rules](#inhibit_rules)
  - [time_intervals](#time_intervals)
- [prometheus alerting config](#prometheus-alerting-config)
- [docker compose](#docker-compose)


## alertmanager

alertmanager는 [프로메테우스 조직에서 제공하는 알림 관리 도구](https://github.com/prometheus/alertmanager)로 프로메테우스에서 설정된 alerting rules를 통해 알람이 발생하면 alertmanager가 이를 수신하여 이메일, 페이저듀티, 슬랙, 웹 훅 등 다양한 채널로 알림을 전송한다

#### 주요 기능

알람 수신 및 처리: 프로메테우스로부터 알람을 수신하고 여러 알림 채널로 전달한다

알람 그룹화: 유사한 알람을 하나의 알림으로 묶어서 전송할 수 있다

알람 소멸 관리: 일정 시간이 지나거나 조건이 해제되면 자동으로 알람을 닫는다

재시도 로직: 알림 전송에 실패했을 때 자동으로 재시도한다

알람 억제: 유사한 알림이 중복해서 전송되지 않도록 조절한다

라우팅: 특정 조건에 맞는 알람을 다른 채널로 전달한다


## alertmanager.yml

[alertmanager configuration docs](https://prometheus.io/docs/alerting/latest/configuration/#configuration)

alertmanager.yml 파일 통해 alertmanager의 기능과 관련된 설정을 정의할 수 있다

```yaml
global: # alertmanager 전역 설정

templates: # 커스텀 템플릿 파일 지정

route: # 라우팅 설정

receivers: # 알림 수신자 설정

inhibit_rules: # 알림 억제 규칙 설정

mute_time_intervals: # 라우팅을 무시할 주기 설정 (deprecated 처리되었으며 대신 time_intervals 사용)

time_intervals: # 라우팅 무시 또는 활성화할 주기 설정
```

### global

alertmanager가 알림 채널에 알림을 전송하기 위한 기본 설정 값을 정의하며 receivers에서 오버라이딩할 수 있다

이메일 smtp, jira api, slack api, pagerduty uri 등을 설정할 수 있다

```yaml
global:

  # 알람이 해제된 후 알림 전송까지의 시간
  resolve_timeout: 5m  

  # 이메일 알림 전송을 위한 smtp 설정 (gmail smtp 사용)
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alertmanager@example.com'
  smtp_auth_username: 'your-email@gmail.com'
  smtp_auth_password: 'your-email-password'
  smtp_require_tls: true
```

### route

알람 그룹화와 라우팅할 규칙을 설정한다

모든 라우터가 알람과 매칭되지 않을 경우 설정한 기본 수신자에게 알림을 전송한다

```yaml
route:
  
  # 기본 수신자 설정
  # 알림이 모든 routes에 매칭되지 않는 경우 'default' 라는 이름의 receiver에게 알림을 전달한다 
  receiver: 'default'

  # 알람 그룹화 설정
  group_by: ['alertname', 'severity'] # 알람을 그룹화할 라벨
  group_wait: 30s                     # 첫 번째 알람 후 추가 알람을 기다리는 시간
  group_interval: 5m                  # 그룹화된 알람이 다시 전송되는 주기
  repeat_interval: 1h                 # 동일한 알람이 반복 전송되는 간격

  # 라우팅 규칙
  routes:
    -
      # 특정 라우트마다 라벨을 통해 다시 그룹화할 수 있다
      group_by: ['service']
      # 알림이 아래의 라벨에 매칭되는 경우 'slack-critical' 라는 수신자에게 알림 전송
      matchers:
        - severity="critical"
      # 매칭된 이후 다음 라우팅 규칙도 적용할지 설정한다. false로 설정하면 여기서 매칭을 멈춘다 (필요에 따라 중복 알림을 방지할 수 있음)
      continue: true                  
      
    -
      # 알림이 아래의 라벨에 매칭되는 경우 'email-notification' 라는 수신자에게 알림 전송
      matchers:
      - alertname="InstanceDown|HighMemoryUsage"
      receiver: 'email-notification'
```

### receivers

웹 훅, 이메일, 슬랙, 지라, 페이저 듀티 등 알림을 전송받을 채널들을 설정한다

```yaml
receivers:
    
  # 기본 수신자 설정 (웹 훅)
  -
    name: 'default'
    webhook_configs:
      -
        url: 'http://localhost:5001/webhook'

  # 이메일 알림 수신자 설정
  -
    name: 'email-notification'
    email_configs:
      -
        to: ${RECEIVER_EMAIL}
        from: ${SENDER_EMAIL}
        smarthost: ${SMTP_HOST}
        auth_username: ${SMTP_USERNAME}
        auth_password: ${SMTP_PASSWORD}
        require_tls: true

  # 슬랙 알림 수신자 설정
  -
    name: 'slack-critical'
    slack_configs:
      -
        api_uri: ${SLACK_API_URI}
        channel: '#alerts'
        send_resolved: true
        title: '{{ .CommonLabels.alertname }} ({{ .Status }})'
        text: 'Description: {{ .CommonAnnotations.description }}\nDetails: {{ .CommonAnnotations.summary }}'
```


### inhibit_rules

알람의 존재 여부에 따라 다른 알람을 억제할 규칙을 정의한다

즉, 우선순위가 높은 알람이 발생했을 때 낮은 우선순위의 알람을 억제한다

```yaml
inhibit_rules:
    
  # 'critical' 등급의 알람을 전송받으면 'warning' 알람을 억제한다
  -
    source_matchers:                 # 억제하는 조건이 되는 알람을 정의한다
      - alertname: "InstanceDown"       
    target_matchers:                 # 억제 대상이 되는 알람을 정의한다
      - alertname: "HighMemoryUsage"
    equal: ['instance']              # source와 target이 동일한 라벨 값을 가져야 하는 라벨 목록을 지정한다
                                     # instance 값이 동일해야 억제 규칙이 적용된다
                                     # 같은 인스턴스의 메모리 사용량 경고(HighMemoryUsage)는 해당 인스턴스의 다운 경고 알람(InstanceDown)이 있는 경우 무시된다 
```

### time_intervals

라우팅을 무시하거나 활성화할 시간 간격(UTC)을 설정한다

[time_interval config](https://prometheus.io/docs/alerting/latest/configuration/#time_interval)

```yaml
time_interval:
  -
    # 05시부터 시작하여 24시까지만 라우팅하도록 설정
    name: 'example-time-interval'
    time_intervals:
      -
        times:
          - start_time: '05:00'
            end_time: '24:00'
```


## prometheus alerting config

아래와 같이 프로메테우스 설정 파일(prometheus.yml) 파일에 alertmanager 관련 설정을 정의한다

```yaml
alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']
```


## docker compose

prometheus와 alertmanager 컨테이너를 각각 띄우는 도커 컴포즈 파일 작성

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

  alertmanager:
    image: prom/alertmanager
    volumes:
      - ./alertmanager/alertmanager.yml:/etc/alertmanager/alertmanager.yml
    ports:
      - '9093:9093'
    command:
      - '--config.file=/etc/alertmanager/alertmanager.yml'

volumes:
  prometheus-vol:
    external: true
```

