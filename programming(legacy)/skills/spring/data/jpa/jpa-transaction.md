---
layout: default
title:
---

[Transaction](#transaction)

[JPA Transaction](#jpa-transaction)

[EntityManager EntityTransaction](#entitymanager-entitytransaction)

[JPA Transaction Propagation](#jpa-transaction-propagation)

## Transaction

트랜잭션은 하나의 작업 단위를 의미하는데, 여러 작업을 묶어 논리적으로 하나의 작업처럼 처리할 수 있음

#### 트랜잭션 ACID 속성
- Atomicity: 모든 작업 성공 or 실패(원자성)
- Consistency: 트랜잭션 전후 데이터 일관성 유지
- Isolation: 트랜잭션 간 독립성 보장
- Durability: 트랜잭션 커밋 후 영구히 저장

## JPA Transaction

JPA를 이용하여 트랜잭션을 관리할 수 있음
- 데이터 조작 작업(삽입, 수정, 삭제 등)
- 데이터베이스 상태 일관성 유지
- 작업 중 오류가 발생하면 변경사항 롤백

JPA 트랜잭션 관련 객체: EntityManager, EntityTransaction

## EntityManager, EntityTransaction

EntityManager는 데이터베이스와의 연결을 관리하며 트랜잭션 경계를 설정함 (명시적인 트랜잭션 관리)

`persist` `merge` `remove`와 같은 작업은 트랜잭션 내에서 실행됨

엔티티 매니저는 트랜잭션이 끝나면 더 이상 사용할 수 없음

트랜잭션 범위 내에서만 엔티티를 조작할 수 있으며 트랜잭션 커밋 시 영속성 컨텍스트에 관리되고 있는 엔티티들이 데이터베이스에 반영됨

#### 예시 코드

```java
/*
    EntityTransaction 객체 반환 메서드
    EntityTransaction의 begin(), commit(), rollback() 메서드를 통해 명시적으로 트랜잭션을 관리함
*/
EntityTransaction transaction = entityManager.getTransaction();

try {
    // 트랜잭션 시작
    transaction.begin();
    
    // 영속성 컨텍스트에 엔티티 영속화, 트랜잭션 범위 내에서만 엔티티 매니저 사용
    entityManager.persist(simpleEntity);
    
    //트랜잭션 커밋
    transaction.commit();
} catch (Exception e) {
    
    // 예외 발생 시 트랜잭션 롤백
    transaction.rollback();
} finally {
    
    // 엔티티 매니저 종료
    entityManager.close();
}
```

## JPA Transaction Isolation Level

JPA는 기본적으로 데이터베이스의 격리 수준을 따름

별도의 트랜잭션 격리 수준 설정을 지원하지 않으며, 데이터베이스 또는 스프링으로 설정할 수 있음

## JPA Transaction Propagation

JPA 자체에서 트랜잭션 전파 기능을 지원하지 않음

스프링의 트랜잭션 관리로 보완할 수 있음