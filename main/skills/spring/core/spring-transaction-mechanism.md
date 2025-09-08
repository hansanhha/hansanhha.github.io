---
layout: default
title:
---

[Spring Transaction](#spring-transaction)

[Spring Transaction Mechanism](#spring-transaction-mechanism)
- [Transaction Abstraction](#spring-transaction-mechanism-transaction-abstraction)
- [Transaction Synchronization](#spring-transaction-mechanism-transaction-synchronization)
- [Global Transaction, Local Transaction](#spring-transaction-mechanism-global-transaction-local-transaction)
- [Physical Transaction, Logical Transaction](#spring-transaction-mechanism-physical-transaction-logical-transaction)
- [Spring Transaction Mechanism: Savepoint](#spring-transaction-mechanism-savepoint)
- [Transaction Propagation](#spring-transaction-mechanism-transaction-propagation)
- [Transaction Isolation](#spring-transaction-mechanism-transaction-isolation)
- [@Transactional - Declarative Transaction Management](#spring-transaction-mechanism-transactional---declarative-transaction-management)

## Spring Transaction

스프링은 스프링의 트랜잭션 관리 및 JPA와 통합하여 비즈니스 코드에서 명시적으로 트랜잭션을 관리하는 [일반적인 JPA 방식](../../../data/jpa/txt/jpa%20transaction.md#예시-코드)과 달리 

선언적으로 트랜잭션을 관리하거나 영속성 컨텍스트 변경사항을 자동으로 반영하는 메커니즘, 트랜잭션 리소스 관리 자동화 등의 기능을 제공한다

주요 컴포넌트: `@Transactional` `JpaTransactionManager` `TransactionSynchronizationManager`

## Spring Transaction Mechanism

### Spring Transaction Mechanism: Transaction Abstraction

스프링은 트랜잭션을 추상화함으로써 단순히 데이터베이스 트랜잭션만을 지원하는 것을 넘어서 메시징 시스템, 파일 시스템 등 여러 리소스에서 트랜잭션을 일관적으로 관리할 수 있도록 지원한다

#### 트랜잭션 상태 추상화
- [TransactionDefinition](./transaction%20objects.md#transactiondefinition): 트랜잭션 설정 정보 보관(이 정보를 바탕으로 트랜잭션 생성)
- [TransactionStatus](./transaction%20objects.md#transactionstatus): 생성된 트랜잭션에 대한 상태 정보 보관 및 제어

#### 트랜잭션 관리 추상화
- [PlatformTransactionManager](./PlatformTransactionManager.md): 트랜잭션 관리(트랜잭션 시작/커밋/롤백)

### Spring Transaction Mechanism: Transaction Synchronization

스프링에서 제공하는 트랜잭션 동기화 기술은 **개발자가 작성해야 할 비즈니스 로직**과 **트랜잭션 관리 및 리소스 관리 코드**를 **분리**하기 위한 목적을 가진다

스프링은 트랜잭션 경계 안에서 자동으로 리소스를 바인딩/해제하여 리소스 누수를 방지하고, 동일한 리소스를 재사용하여 일관적이고 효율적인 리소스 관리를 보장한다

또한 트랜잭션 시작과 종료 전/후에 호출되는 트랜잭션 콜백 인터페이스를 제공하여 스프링의 동기화 작업 외에 개발자가 트랜잭션 및 리소스와 관련한 추가 작업을 진행할 수 있도록 지원한다  

**비즈니스 로직과 트랜잭션 관리 및 리소스 관리 코드 분리**
- 비즈니스 로직 실행(스프링): 트랜잭션 시작 또는 전파, 데이터베이스 작업을 수행하기 위한 데이터베이스 커넥션, JDBC 리소스, 하이버네이트 세션 등의 리소스를 현재 스레드에 바인딩
- 비즈니스 로직(개발자): 개발자가 작성한 로직 수행
- 비즈니스 로직 실행 후(스프링): 트랜잭션 종료(커밋/롤백 처리), 바인딩된 리소스를 스레드에서 제거하고 리소스 해제 

#### 트랜잭션 동기화 주요 사용 사례

- JDBC 커넥션 관리
  - DataSourceTransactionManager는 JDBC 커넥션을 스레드 로컬에 바인딩하고 관리함
  - 트랜잭션 경계 내에서 동일한 커넥션 사용
- Hibernate 세션 관리
  - HibernateTransactionManager는 Hibernate 세션을 스레드 로컬에 저장하고 관리함
  - 트랜잭션 경계 내에서 일관된 세션 유지
- 트랜잭션 이벤트
  - 트랜잭션 작업 중 전/후처리 작업, 캐시 갱신, 알림 전송 등 수행
- 스프링 배치
  - 스프링 배치 작업에서 트랜잭션 동기화를 통해 단계별 작업의 트랜잭션 관리

#### 주요 객체

[TransactionSynchronizationManager]()

[TransactionSynchronization]()

[TransactionSynchronizationUtils]()

### Spring Transaction Mechanism: Global Transaction, Local Transaction

스프링은 트랜잭션을 **트랜잭션 범위**와 **대상 리소스**에 따라 **전역 트랜잭션**과 **지역 트랜잭션**으로 구분한다

#### Global Transaction (JTA)

여러 리소스(여러 데이터베이스, 메시지 큐, 파일 시스템 등)에 걸쳐 일관성을 보장하는 하나의 분산 트랜잭션으로, JTA(Java Transaction API)를 구현한 분산 트랜잭션 매니저를 통해 제어한다

전역 트랜잭션은 2PC 프로토콜을 사용하여 모든 리소스에 대한 커밋/롤백 여부를 조율한다
- 2PC (Two Phase Commit)
  - Prepare 단계: 모든 리소스에 대한 커밋 준비 요청
  - Commit 단계: 모든 리소스가 커밋 준비를 마치면 커밋, 실패 시 롤백

분산 환경에서 일관성을 유지하지만 네트워크 오버헤드와 복잡도가 높다

#### Local Transaction

하나의 리소스(단일 데이터베이스, 단일 메시지 큐 등)에 국한된 트랜잭션으로, JpaTransactionManager(데이터베이스 트랜잭션 매니저) 같은 특정 리소스용 트랜잭션 매니저를 통해 제어함

트랜잭션 매니저가 리소스에 직접 접근해 트랜잭션을 관리한다

### Spring Transaction Mechanism: Physical Transaction, Logical Transaction

스프링은 트랜잭션을 **트랜잭션 구성 및 방식**에 따라 **물리 트랜잭션**과 **논리 트랜잭션**으로 구분한다

물리/논리 트랜잭션 구분 이유
- 트랜잭션 전파 지원
- 중첩 트랜잭션 지원
- 리소스 재사용
- 일관된 트랜잭션 관리

#### Physical Transaction

데이터베이스 등 실제 리소스의 트랜잭션 상태를 직접 제어하는 트랜잭션

JDBC 드라이버를 통해 `Connection.commit()` `Connection.rollback()`을 호출할 때 발생하는 실제 트랜잭션

물리 트랜잭션은 데이터베이스 커넥션 수준에서 제어되며, 물리적으로 하나의 트랜잭션을 의미한다

여러 논리 트랜잭션이 하나의 물리 트랜잭션에 포함될 수 있다

#### Logical Transaction

스프링에서 트랜잭션 경계를 제어하기 위해 트랜잭션 관리 계층에서 관리하는 트랜잭션으로 **트랜잭션 전파**나 **중첩 트랜잭션** 같은 기능이 논리 트랜잭션을 통해 관리된다

하나의 물리 트랜잭션에 여러 논리 트랜잭션이 포함될 수 있으며 savepoint를 통해 중첩 트랜잭션과 같은 트랜잭션 간 논리적 경계를 설정할 수 있다

### Spring Transaction Mechanism: Savepoint

Savepoint는 데이터베이스 트랜잭션 내에서 특정 시점(체크 포인트)을 설정하면 트랜잭션 전체를 롤백하지 않고 해당 시점까지 롤백을 할 수 있는 메커니즘이다

트랜잭션이 활성화된 상태에서 트랜잭션 내에 여러 개의 savepoint를 설정할 수 있다

savepoint를 설정한 이후에 발생한 변경 사항만 롤백(부분 롤백)할 수 있으며, 특정 savepoint로 돌아가 트랜잭션을 계속 진행할 수도 있다

스프링은 [DefaultTransactionStatus](./transaction%20objects.md#defaulttransactionstatus) (실질적으론 [AbstractTransactionStatus](./transaction%20objects.md#abstracttransactionstatus---savepoint-상태-처리))를 사용하여 Savepoint를 관리한다

savepoint 기능을 사용하려면 비즈니스 로직에서 프로그래밍 방식으로 직접 처리해야 한다

#### Savepoint 사용 사례

대규모 배치 작업
- 여러 단계로 이루어진 배치 작업에서 중간 상태 저장
- 실패 시 전체 트랜잭션을 취소하지 않고 savepoint로 복원

정말 복잡한 비즈니스 로직
- 여러 단계로 구성된 비즈니스 로직에서 일부 작업이 실패했을 때, 이전 단계로 복귀

테스트 및 디버깅
- 트랜잭션 상태 저장 후, 특정 작업의 영향을 확인하거나 테스트

#### Savepoint 한계

DB 엔진에서 지원해야 savepoint를 사용할 수 있다

트랜잭션 상태를 메모리나 로그에 저장하므로 추가적인 자원을 소모한다

주로 로컬 트랜잭션에서만 사용 가능

### Spring Transaction Mechanism: Transaction Propagation

스프링은 트랜잭션을 데이터베이스 커넥션 수준에서 사용되는 물리 트랜잭션과 스프링 내부에서 사용되는 논리 트랜잭션으로 구분지어

**논리 트랜잭션 간 트랜잭션 경계를 전파하는 방식**을 결정할 수 있는 트랜잭션 전파 메커니즘을 제공한다

트랜잭션을 시작할 때 트랜잭션 전파 옵션을 지정하면 해당 옵션에 따라 기존 트랜잭션에 참여하거나 새로운 트랜잭션을 생성하는 등 트랜잭션 행동을 결정한다

[트랜잭션 전파 설정값](./transaction%20objects.md#transactiondefinition)

### Spring Transaction Mechanism: Transaction Isolation

트랜잭션 격리 수준은 트랜잭션이 독립적으로 동작할 수 있도록 서로 간섭받지 않는 정도를 의미하며 동시성 문제를 제어하는 방법이다

격리 수준에 따라 데이터 일관성 수준과 데이터베이스 처리 성능이 바뀐다

#### 여러 트랜잭션이 동시에 실행될 때 발생할 수 있는 동시성 문제

1. Dirty-Read: 다른 트랜잭션이 커밋되지 않은 데이터를 읽는 문제
2. Non-Repeatable-Read: 같은 데이터를 여러 번 읽을 때, 다른 트랜잭션이 해당 데이터를 수정하고 커밋하면 데이터가 달라지는 문제
3. Phantom-Read: 같은 조건으로 조회했을 때, 다른 트랜잭션이 데이터를 추가하거나 삭제해서 조회 결과가 달라지는 문제

#### 동시성 문제를 해결하기 위한 격리 수준(ANSI SQL)

|             격리 수준              |                 설명                 |                      동시성 문제 해결 여부                      |         데이터베이스 벤더 기본값         |
|:------------------------------:|:----------------------------------:|:------------------------------------------------------:|:-----------------------------:|
| READ_UNCOMMITTED(lowest level) |        커밋되지 않는 데이터를 읽을 수 있음        |  Dirty-Read, Non-Repeatable-Read, Phantom-Read 발생 가능   |                               |
|         READ_COMMITTED         |          커밋된 데이터만 읽을 수 있음          | Dirty-Read 방지, Non-Repeatable-Read, Phantom-Read 발생 가능 | Oracle, SQLServer, PostgreSQL |
|        REPEATABLE_READ         | 트랜잭션 내에서 동일 데이터를 읽으면 항상 동일한 결과만 반환 | Dirty-Read, Non-Repeatable-Read 방지, Phantom-Read 발생 가능 |             MySQL             |
|  SERIALIZABLE(highest level)   |          트랜잭션이 순차적으로 실행됨           |             Dirty Read, Non-Repeatable Read, Phantom Read 방지                                           |                               |

#### 스프링의 격리 수준 제어

[Spring Isolation](./transaction%20objects.md#isolation)

#### 스프링이 격리 수준을 제어할 수 있는 이유

스프링은 [트랜잭션 추상화](#spring-transaction-mechanism-transaction-abstraction) 및 [트랜잭션 동기화](#spring-transaction-mechanism-transaction-synchronization)을 통해 다양한 트랜잭션 관리 기능을 추상화하고 있기 때문에 격리 수준을 설정할 수 있다

[PlatformTransactionManager](./PlatformTransactionManager.md) 인터페이스를 중심으로 JPA, JDBC, Hibernate, JTA 등 다양한 트랜잭션 매니저를 통합적으로 지원하는데,

JPA에서 사용하는 JpaTransactionManager는 트랜잭션을 시작할 때 사용할 데이터베이스 커넥션 객체를 확보한다(스레드 바인딩)

확보한 커넥션에 `connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);` 와 같은 작업을 수행하여 데이터베이스 커넥션의 격리 수준을 설정한다

JPA 자체에서 격리 수준 설정을 제공하지 않지만 스프링의 트랜잭션 추상화를 통해 JPA를 통해 격리 수준을 설정할 수 있다

설정된 격리 수준은 해당 커넥션에서만 유지되고, 같은 커넥션을 공유하는 트랜잭션은 동일한 격리 수준을 갖게된다

### Spring Transaction Mechanism: @Transactional - Declarative Transaction Management

스프링은 제공하는 `@Transactional` 어노테이션은 트랜잭션을 코드에 명시적으로 작성하지 않고 선언하는 것만으로 트랜잭션 경계를 설정할 수 있게 한다

AOP(Aspected-Oriented Programming)를 기반으로 동작하며 **비즈니스 로직**으로부터 **트랜잭션 처리 로직**을 **분리**한다 

[@Transactional](./@Transactional.md)

[@Transactional Test](./@Transactional%20test.md)
