---
layout: default
title:
---

#### index
- [MDC](#mdc)
- [trace context](#trace-context)


## MDC

MDC 란 Mapped Diagnostic Context의 약자로 요청에 대한 컨텍스트 정보를 유지할 수 있는 저장소를 말한다

이러한 기능이 필요한 이유는 observability 컴포넌트가 해당 요청(스레드)의 고유 정보에 접근할 수 있도록 지원하기 위함이다 

MDC가 제공하는 컨텍스트는 스레드 로컬 범위에서 동작하여 각 스레드 별로 독립적인 컨텍스트를 유지하며, 동일한 요청 내에서는 같은 MDC 데이터를 공유하지만 다른 요청(다른 스레드)에서 격리된다

MDC 컨텍스트에 trace id, span id, usere id, trasaction id 등과 같은 고유 정보를 저장하면 애플리케이션의 각 observability 컴포넌트(메트릭/추적/로깅 시스템)가 해당 정보를 활용할 수 있다


## trace context

trace context는 멀티 스레드 및 분산 환경에서 각 요청이나 사용자 컨텍스트의 정보를 유지하거나 observability 데이터(MDC context)와 연결하는 역할을 수행한다

이러한 역할이 필요한 이유가 뭘까?

서비스에서 여러 개의 로그를 발생시키거나 분산 시스템에서 로그가 발생하는 경우 단순히 사람이 읽을 수 있는 로그 메시지를 이용하여 전체 흐름을 파악하기 어렵다

인증, 결제 서비스가 각각 분리되어 있는 시스템에서 사용자의 결제 요청에 대한 에러 로그를 출력하면 일반적으로 아래와 같을 것이다

로그 메시지는 이벤트에 대한 정보만 담고 있어서 각 로그를 통해 어느 로그가 전체 요청에 대한 일부 정보를 나타내는 건지 알 수 없다

```text
[INFO]  [thread-10] [auth service   ] user logged in
[INFO]  [thread-1]  [payment service] payment processed
[INFO]  [thread-2]  [auth service   ] user logged in
[ERROR] [thread-1]  [payment service] database timeout 
[INFO]  [thread-8]  [payment service] payment processed
[ERROR] [thread-8]  [payment service] database timeout 
```

MDC가 해당 스레드에 컨텍스트 정보를 유지하여 각 observability 컴포넌트가 해당 고유 정보에 접근할 수 있도록 한다면, trace context는 분산 환경 전체(멀티 서비스/스레드)에서 네트워크 요청 등을 통해 컨텍스트 정보들을 연결할 수 있게 한다

주로 http 헤더, 메시지 큐, grpc metadata 등에 정보를 저장한다

```text
GET /shallowOrder-service/orders
Headers:
X-Trace-Id: abcd-1234-efgh-5678
X-User-Id: 98765
```

MDC는 자동 전파 기능을 제공하지 않기에 멀티 스레드 작업 시 직접 전달해줘야 하고 trace context는 observability 도구에서 자동 전파 기능을 지원한다 







