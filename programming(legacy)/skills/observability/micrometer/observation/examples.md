---
layout: default
title:
---

#### index
- [instrumentation two concepts](#instrumentation-two-concepts)
- [instrumentation of http communication](#instrumentation-of-http-communication)
- [instrumentation of messaging communication](#instrumentation-of-messaging-communication)
- [instrumentation of thread switching components](#instrumentation-of-thread-switching-components)


## instrumentation two concepts

계측이란 시스템이나 코드에 모니터링을 위한 측정 도구를 삽입하는 작업을 의미하는데, 이는 다시 두 가지 개념으로 세분화된다

애플리케이션은 마이크로서비스, 비동기 처리, 다중 스레드 환경에서 하나의 요청이 여러 컴포넌트들을 거친다

이 때 각 컴포넌트의 메트릭을 개별적으로 수집하면 전체 흐름을 파악하기 어려워지기 때문에 전체 흐름을 유지한 채로 성능 파악, 추적 등의 계측 작업을 수행하기 위해 아래의 개념을 차용한다

#### 1. context propagation
 
기존 컨텍스트를 스레드 또는 네트워크를 통해 전파하는 것을 컨텍스트 전파라고 한다

요청의 컨텍스트(trace id, 세션 정보 등)를 전파하여 호출 체인 전체에서 동일한 컨텍스트를 사용하게 한다 

컨텍스트를 전파함으로써 계측 시스템 간 동일한 컨텍스트를 사용하여 데이터의 일관성을 유지하게 되어 연관된 분석을 진행할 수 있다

micrometer는 컨텍스트를 정의하고 스레드를 통해 전파하기 위해 micrometer context propagation 이라는 라이브러리를 사용한다

또한 micrometer tracing 핸들러와 함께 `SenderContext`와 `ReceiverContext` 객체를 사용하여 전파되는 Observation 컨텍스트 객체를 생성한다

#### 2. creation of observations

특정 작업을 측정하기 위해 Observation을 생성할 때 부모 Observation 객체가 있는지 확인하고 Observation 간 부모-자식 관계를 유지하여 하위 작업이 상위 작업의 일부로 연결시킨다

각 observation에 메트릭을 기록하고 부모-자식 관계를 통해 연관된 작업을 그룹화하면 특정 메서드나 서비스의 응답 시간이 느려지는 문제가 발생했을 때, 지연의 원인을 쉽게 분석할 수 있다


== 나중에 내용 추가




## instrumentation of http communication



## instrumentation of messaging communication



## instrumentation of thread switching components
