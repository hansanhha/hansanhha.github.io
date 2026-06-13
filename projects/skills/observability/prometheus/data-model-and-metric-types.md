---
layout: default
title:
---

#### index
[data model](#data-model)
- [metric names](#metric-names)
- [metric labels](#metric-labels)
- [samples](#samples)
- [notation](#notation)

[metric types](#metric-types)
- [Counter](#counter)
- [Gauge](#gauge)
- [Histogram](#histogram)
- [Summary](#summary)


## data model

프로메테우스는 기본적으로 모든 데이터를 시계열 형태로 저장한다

시계열은 동일한 메트릭과 동일한 레이블에 속하는 타임스탬프가 지정된 연속된 값으로 **시간에 따른 데이터 값의 변화(연속된 데이터 포인트)**를 나타낸다

수집된 데이터에 대한 시계열 외에도 프로메테우스는 쿼리의 결과로 임시 파생 시계열을 생성할 수 있다

또한 모든 시계열 데이터는 자신의 메트릭 이름과 선택적으로 가질 수 있는 라벨(키-값 쌍)을 통해 고유하게 식별된다

### metric names

메트릭 이름은 시스템에서 측정하는 기능에 대한 일반적인 이름을 나타낸다

아스키 문자, 숫자, 언더스코어, 콜론을 사용할 수 있다 (참고로 콜론은 커스텀 기록 규칙- user defined recording rules -을 위해서만 사용할 수 있다)

e.g) 총 http 요청 횟수에 대한 메트릭 이름: `http_requests_total`


### metric labels

동일한 메트릭 이름을 가진 메트릭들을 기준에 따라 좀 더 상세하게 구분지을 수 있도록 메트릭에 키-값 쌍의 라벨을 부여할 수 있다

**라벨 조합에 따라 동일한 메트릭 이름이라도 다른 시계열로 구분된다**

또한 라벨을 활용하면 promql을 통해 필터링, 집계, 분류가 용이해진다

아스키 문자, 숫자, 언더스코어를 통해 라벨을 만들 수 있으며 라벨을 추가하거나 제거하는 등의 라벨 값을 변경하면 새 시계열이 생성된다

라벨 값은 유니코드 문자를 포함할 수 있는데, 비어 있는 값을 가진 라벨은 존재하지 않는 것으로 간주된다

e.g) http 메서드, 상태 값, 서비스 이름에 따른 총 http 요청 횟수: `http_request_total{method="GET", status="200", service="user-service""}`

### samples

samples는 메트릭의 실제 시계열 데이터를 나타내며 부동소수점(float64) 형식과 밀리초 단위의 타임스탬프로 저장된다

### notation

메트릭 이름과 라벨을 표기하는 방법으로 OpenTSDB와 동일한 표기법을 사용한다

`<metric name>{<label name>=<label value>, ...}`

`api_http_requests_total{method="POST", handler="/messages"}`


## metric types

프로메테우스 클라이언트 라이브러리는 네 가지 메트릭 유형을 제공한다

해당 메트릭 유형들은 클라이언트 라이브러리와 와이어 프로토콜에서만 사용되고 프로메테우스 서버는 아직 타입 정보를 사용하지 않고 모든 데이터를 untyped 시계열로 평탄화한다

프로메테우스 공식 문서에서는 이러한 구조가 향후 변경될 수 있다는 여지를 남겨두고 있다 [참고](https://prometheus.io/docs/concepts/metric_types/)

### Counter

단조롭게 증가하는 단일 카운터를 나타내는 누적 메트릭으로 누적되는 이벤트나 횟수를 기록할 때 사용한다

값은 증가만 할 수 있으며 재시작 시에만 값이 0으로 초기화된다

감소할 수 있는 값인 경우 Gauge를 대신 사용해야 한다

e.g
- 수신한 요청 수, 작업 완료 수, 에러 발생 수 등
- `http_requests_total{method="GET"}`

### Gauge

임의적으로 올라가고 내려갈 수 있는 단일 수치 값(현재 상태)을 나타내는 메트릭으로 변화하는 값의 현재 상태를 모니터링할 때 사용한다

값이 증가하고 감소할 수 있는 count 값에도 사용될 수 있다

e.g
- 메모리 사용량, 동시성 요청 수 등
- `memory_usage_bytes{app="web"}`

### Histogram

관측된 값의 분포를 버킷에 나누어 카운트(`<basename>_count>`)하고, 관찰된 모든 값의 합계(`<basename>_sum`)를 제공하는 메트릭으로 시간 분포, 크기 분포를 분석할 때 사용한다

히스토그램은 스크랩하는 동안 `<basename>`을 기반으로 여러 시계열을 노출한다

e.g
- api 응답 시간, 데이터 전송 크기, 처리 시간 등
- `http_request_duration_seconds_bucket{le="0.1"}`

[histogram 사용법](https://prometheus.io/docs/practices/histograms/)

### Summary

히스토그램과 마찬가지로 백분위를 제공하고 추가적으로 직접 계산된 사분위수(quartile) 및 합계 데이터를 제공하는 메트릭으로 정확한 백분위 데이터가 필요할 때 사용한다

e.g
- 응답 시간 백분위, 사용자 세션 지속 시간 등
- `http_request_duration_seconds{quantile="0.95"}`

[summary 사용법](https://prometheus.io/docs/practices/histograms/)



