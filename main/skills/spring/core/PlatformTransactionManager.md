---
layout: default
title:
---

## Transaction Managers

트랜잭션 및 트랜잭션 리소스 동기화를 관리하는 스프링 트랜잭션의 핵심 객체로 모든 트랜잭션 매니저 구현체는 스프링에서 제공하는 `TransactionManager` 최상위 마커 인터페이스를 확장함

spring data jpa 기준 트랜잭션 매니저 구현체 상속 관계도
```text
TransactionManager
-\ PlatformTransactionManager
--\ AbstractPlatformTransactionManager
---\ JpaTransactionManager
```

## PlatformTransactionManager

스프링의 트랜잭션 관리 중앙 인터페이스

생각보다 몇 개 되지 않는 메서드만 정의되어 있지만 사악한 주석으로 메서드 행동을 설명하고 있음

[자식 추상 클래스 AbstractPlatformTransactionManager](./AbstractPlatformTransactionManager.md)

```java
public interface PlatformTransactionManager extends TransactionManager {

    /*
        파라미터로 주어진 TransactionDefinition의 propagation 설정값에 따라서
        현재 활성화된 트랜잭션 또는 새로 생성한 트랜잭션을 반환함
        
        또한 isolation level이나 timeout 설정 값은 
        새로운 트랜잭션에만 적용되고 이미 참여하고 있는 트랜잭션에는 무시됨
     */
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    /*
        getTransaction 메서드에서 반환한 TransactionStatus의 설정값에 따라 커밋을 수행함
        rollback-only 옵션이 설정되어 있는 경우 롤백을 수행함
     */
    void commit(TransactionStatus status) throws TransactionException;

    // 주어진 transaction 롤백
    void rollback(TransactionStatus status) throws TransactionException;
}
```