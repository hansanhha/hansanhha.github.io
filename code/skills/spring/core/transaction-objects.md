---
layout: default
title:
---

[Transaction Objects](#transaction-objects)

[TransactionDefinition](#transactiondefinition)

[Isolation](#isolation)
 
[TransactionStatus](#transactionstatus)

[AbstractTransactionStatus](#abstracttransactionstatus)

[DefaultTransactionStatus](#defaulttransactionstatus)

[SimpleTransactionStatus](#simpletransactionstatus)

## Transaction Objects

스프링은 트랜잭션을 책임/역할에 따라 여러 가지 객체로 분리하여 제공한다

트랜잭션 매니저 구현체에 따라 각 객체를 구현한다

**TransactionDefinition**: 트랜잭션 설정 정보 보관, 이 정보를 바탕으로 트랜잭션 생성

**TransactionStatus**: 생성된 트랜잭션에 대한 상태 정보 보관 및 제어

**SavepointManager**: 트랜잭션 savepoint 제어

**SmartTransactionObject**: 트랜잭션 rollback-only 설정 확인

## TransactionDefinition

트랜잭션의 속성을 정의한 인터페이스

이 정보를 바탕으로 트랜잭션을 생성한다

설정 종류에 따라 상수값을 정의하고 모든 메서드를 기본 값을 반환하는 default 메서드로 정의함

설정 정보(필드 이름)
- **트랜잭션 이름**(`name`)
- **트랜잭션 전파**(`PROPAGATION_*`): 새로운 트랜잭션 생성, 기존 트랜잭션 참여 여부 설정 및 트랜잭션이 없는 상태에서의 처리 방식 정의
- **격리 수준**(`ISOLATION_*`): 데이터베이스 격리 수준 설정
- **최대 지속 시간**(`TIMEOUT_DEFAULT`): 트랜잭션 최대 지속 시간 설정(시간 내에 트랜잭션이 완료되지 않으면 롤백 처리)
- **읽기 전용**(`readOnly``): 트랜잭션 읽기 전용 설정

스프링은 JPA에서 제공하지 않는 트랜잭션 전파라는 기능을 지원한다

**트랜잭션 전파 설정(PROPAGATION_*) 값으로 TransactionManager와 TransactionSynchronizationManager의 동작을 결정한다**

```java
public interface TransactionDefinition {

    /*------------------------------
           트랜잭션 전파 설정 값
    ------------------------------*/
    
    // 트랜잭션 전파 설정에 따라 트랜잭션 생성/참여, 트랜잭션 동기화 생성/공유 등의 동작 방식이 결정됨
  
    /*
        트랜잭션 전파 기본값
        
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 해당 트랜잭션 컨텍스트 참여
        - 없으면 새 트랜잭션 생성
        
        트랜잭션 동기화
        - 기존 트랜잭션 참여 시 동일한 동기화 리소스 공유
        - 새 트랜잭션 참여 시 새로운 동기화 리소스 초기화 
    */
    int PROPAGATION_REQUIRED = 0;

    /*
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 해당 트랜잭션 컨텍스트 참여
        - 없으면 트랜잭션 없이 실행
        
        트랜잭션 동기화
        - 기존 트랜잭션이 있는 경우만 동일한 동기화 리소스 공유
        - 없으면 동기화 리소스 작업 생략
     */
    int PROPAGATION_SUPPORTS = 1;

    /*
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 해당 트랜잭션 컨텍스트 참여
        - 기존 트랜잭션이 없으면 IllegalTransactionStateException 발생
        
        트랜잭션 동기화
        - 기존 트랜잭션이 있는 경우만 동일한 동기화 리소스 공유
        - 없으면 실행되지 않음
     */
    int PROPAGATION_MANDATORY = 2;

    /*
        독립적인 작업(로깅, 외부 시스템 호출)에 적합한 전파 옵션
        성능 및 복잡성 고려 필요
    
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 일시적으로 보류하고, 새 트랜잭션 생성
        - 새 트랜잭션이 종료되면 보류한 기존 트랜잭션 활성화
        
        트랜잭션 동기화
        - 새로운 트랜잭션에 대한 별도의 동기화 리소스 생성
        - 기존 트랜잭션의 동기화 리소스는 임시로 저장
     */
    int PROPAGATION_REQUIRES_NEW = 3;

    /*
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 일시적으로 보류하고, 트랜잭션 없이 작업 수행
        - 기존 트랜잭션이 없으면 트랜잭션 없이 바로 작업 수행
        
        트랜잭션 동기화
        - 트랜잭션 동기화 리소스 사용 X
        - 기존 트랜잭션 동기화 리소스는 이후 다시 활성화됨
     */
    int PROPAGATION_NOT_SUPPORTED = 4;
    
    /*
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 IllegalTransactionStateException 발생
        - 기존 트랜잭션이 없으면 트랜잭션 없이 바로 작업 수행
        
        트랜잭션 동기화
        - 트랜잭션 동기화 리소스 사용 X
     */
    int PROPAGATION_NEVER = 5;

    /*
        트랜잭션 매니저
        - 기존 트랜잭션이 있으면 SavePoint를 생성하여 중첩 트랜잭션 지원
        - 기존 트랜잭션이 없으면 새 트랜잭션 생성
        
        트랜잭션 동기화
        - 중첩 트랜잭션의 경우 동일한 동기화 리소스 사용, SavePoint를 통해 상태 관리
        - 독립적인 트랜잭션처럼 동작하지만 부모 트랜잭션에 영향받음
     */
    int PROPAGATION_NESTED = 6;
    
    /*------------------------------
           격리 수준 설정 값
    ------------------------------*/

    // 데이터베이스 격리 수준과 동일  
  
    int ISOLATION_DEFAULT = -1;

    int ISOLATION_READ_UNCOMMITTED = 1;

    int ISOLATION_READ_COMMITTED = 2;

    int ISOLATION_REPEATABLE_READ = 4;

    int ISOLATION_SERIALIZABLE = 8;
    
    /*------------------------------
           시간 초과 설정 값
    ------------------------------*/

    int TIMEOUT_DEFAULT = -1;

    /*------------------------------
         설정 값 조회 메서드 정의
    ------------------------------*/

    // 트랜잭션 전파 기본 행동
    default int getPropagationBehavior() {
        return PROPAGATION_REQUIRED;
    }

    // 기본 격리 수준
    default int getIsolationLevel() {
        return ISOLATION_DEFAULT;
    }

    // 기본 시간 초과
    default int getTimeout() {
        return TIMEOUT_DEFAULT;
    }
    
    // 기본 read-only 여부
    default boolean isReadOnly() {
        return false;
    }

    // 기본 트랜잭션 이름
    @Nullable
    default String getName() {
        return null;
    }

    // 기본 설정 값 인스턴스 사용, default 메서드 기본 로직 그대로 사용
    static TransactionDefinition withDefaults() {
        return StaticTransactionDefinition.INSTANCE;
    }
}
```

## Isolation

TransactionDefinition에 명시된 트랜잭션 격리 수준 값을 기반으로 한 enum 클래스

DEFAULT는 데이터베이스의 격리 수준을 따름

[스프링이 격리 수준을 제어할 수 있는 이유](./spring%20transaction.md#스프링이-격리-수준을-제어할-수-있는-이유)

```java
public enum Isolation {

	DEFAULT(TransactionDefinition.ISOLATION_DEFAULT),

	READ_UNCOMMITTED(TransactionDefinition.ISOLATION_READ_UNCOMMITTED),

	READ_COMMITTED(TransactionDefinition.ISOLATION_READ_COMMITTED),

	REPEATABLE_READ(TransactionDefinition.ISOLATION_REPEATABLE_READ),

	SERIALIZABLE(TransactionDefinition.ISOLATION_SERIALIZABLE);

	private final int value;

	Isolation(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

}
```

## TransactionStatus

[예시 코드](../../../data/jpa/src/main/java/hansanhha/transaction/TransactionStatusExample.java)

생성된 트랜잭션에 대한 상태 정보를 보관하고 제어하는 인터페이스로 PlatformTransactionManager와 함께 동작한다

사용 시점
- 트랜잭션 시작
    - `PlatformTransactionManager.getTransaction(TransactionDefinition)` 호출
    - 반환되는 `TransactionStatus` 객체를 통해 트랜잭션 상태 추적
- 트랜잭션 처리
    - 비즈니스 로직 실행 중 `TransactionStatus`를 참조하여 롤백 여부, 새로운 트랜잭션 여부 확인
    - `transactionStatus.isNewTransaction()` `transactionStatus.hasTransaction()` 등
- 트랜잭션 커밋/롤백
    - `commit(TrasactionStatus)` `rollback(TransactionStatus)`

#### 상속 관계

```txt
TransactionExecution, SavepointManager, Flushable
-\ TransactionStatus
```

TransactionStatus는 TransactionExecution, SavepointManager, Flushable 인터페이스를 확장한다

### TransactionExecution

트랜잭션의 현재 상태 정보를 제공하는 인터페이스

TransactionStatus가 구현하며 ReactiveTransaction, TransactionExecutionListener 등에서 사용된다

정의된 메서드(모두 default 메서드로 정의)
- 트랜잭션 이름
- 트랜잭션 활성화 여부
- 새 트랜잭션 여부
- 중첩 트랜잭션 여부
- 읽기 전용 여부
- 롤백 필수 여부
- 완료 여부
- 롤백 설정

```java
public interface TransactionExecution {
    
    default String getTransactionName() {
        return "";
    }
    
    /*    
        활성화된 트랜잭션인지 확인
        새 트랜잭션 또는 기존 트랜잭션에 참여한 경우 true 반환
     */
    default boolean hasTransaction() {
        return true;
    }

    /*
        새 트랜잭션 여부 반환
        중첩 트랜잭션의 경우 트랜잭션 매니저에 따라 새 트랜잭션으로 판단할 수 있기 때문에
        isNested 메서드와 함께 복합 체크
     */
    default boolean isNewTransaction() {
        return true;
    }
    
    default boolean isNested() {
        return false;
    }

    default boolean isReadOnly() {
        return false;
    }

    default void setRollbackOnly() {
        throw new UnsupportedOperationException("setRollbackOnly not supported");
    }

    default boolean isRollbackOnly() {
        return false;
    }

    default boolean isCompleted() {
        return false;
    }
    
}
```

### SavepointManager

Savepoint: 트랜잭션 내에서 특정 시점으로 롤백할 수 있도록 설정하는 기능

SavepointManager는 트랜잭션 저장 지점을 다루는 인터페이스임
- 중간 롤백: 특정 지점까지 작업 수행 후, 오류가 발생하면 저장 지점으로 롤백(일부 작업만 취소)
- 단위 작업 관리

기존 트랜잭션이 있는 상황에서 트랜잭션 전파를 `TransactionDefinition.PROPAGATION_NESTED`로 설정한 경우

Savepoint 기능을 사용하는 트랜잭션 매니저 구현체라면 AbstractTransactionStatus가 확장한 SavepointManager의 API를 사용함

```java
public interface SavepointManager {
    
    // 현재 트랜잭션의 savepoint를 생성하고, savepoint를 나타내는 객체 반환
    Object createSavepoint() throws TransactionException;

    // 특정 savepoint로 롤백(해당 savepoint 이후 모든 작업 취소)
    void rollbackToSavepoint(Object savepoint) throws TransactionException;

    // savepoint 해제
    void releaseSavepoint(Object savepoint) throws TransactionException;
}
```

## AbstractTransactionStatus

TransactionStatus 인터페이스를 일부 구현하며, TransactionStatus 구현체들의 기반이 되는 추상 클래스임

트랜잭션 local rollback-only, complete, SavepointManager 위임 로직 등을 구현함

### AbstractTransactionStatus - 필드

```java
private boolean rollbackOnly = false;

private boolean completed = false;

@Nullable
private Object savepoint;
```

### AbstractTransactionStatus - TransactionExecution 구현 (일부)

```java
@Override
public void setRollbackOnly() {
    if (this.completed) {
        throw new IllegalStateException("Transaction completed");
    }
    this.rollbackOnly = true;
}

@Override
public boolean isRollbackOnly() {
  return (isLocalRollbackOnly() || isGlobalRollbackOnly());
}

// 지역(local) 트랜잭션 rollback-only 설정 값 반환
public boolean isLocalRollbackOnly() {
  return this.rollbackOnly;
}

// 트랜잭션 전체(global)의 rollback-only 설정 값 반환(템플릿 메서드)
public boolean isGlobalRollbackOnly() {
  return false;
}

// 커밋 또는 롤백된 상태 마킹
public void setCompleted() {
  this.completed = true;
}

@Override
public boolean isCompleted() {
  return this.completed;
}
```

### AbstractTransactionStatus - savepoint 상태 처리

```java
@Override
public boolean hasSavepoint() {
    return (this.savepoint != null);
}

// 이 트랜잭션의 savepoint 설정 (PROPAGATION_NESTED 전파 설정 시사용)
protected void setSavepoint(@Nullable Object savepoint) {
  this.savepoint = savepoint;
}

@Nullable
protected Object getSavepoint() {
  return this.savepoint;
}

// 이 트랜잭션에 대한 savepoint 생성 및 보관
public void createAndHoldSavepoint() throws TransactionException {
  setSavepoint(getSavepointManager().createSavepoint());
}

// savepoint로 롤백 후 savepoint 해제
public void rollbackToHeldSavepoint() throws TransactionException {
  Object savepoint = getSavepoint();
  if (savepoint == null) {
    throw new TransactionUsageException(
            "Cannot roll back to savepoint - no savepoint associated with current transaction");
  }
  getSavepointManager().rollbackToSavepoint(savepoint);
  getSavepointManager().releaseSavepoint(savepoint);
  setSavepoint(null);
}

// savepoint 해제
public void releaseHeldSavepoint() throws TransactionException {
  Object savepoint = getSavepoint();
  if (savepoint == null) {
    throw new TransactionUsageException(
            "Cannot release savepoint - no savepoint associated with current transaction");
  }
  getSavepointManager().releaseSavepoint(savepoint);
  setSavepoint(null);
}
```

### AbstractTransactionStatus - SavepointManager 구현

```java
// 템플릿 메서드를 통해 SavepointManager 구현체에 접근하여 savepoint 생성
@Override
public Object createSavepoint() throws TransactionException {
    return getSavepointManager().createSavepoint();
}

// 템플릿 메서드를 통해 SavepointManager 구현체에 접근하여 savepoint로 롤백
@Override
public void rollbackToSavepoint(Object savepoint) throws TransactionException {
  getSavepointManager().rollbackToSavepoint(savepoint);
}

// 템플릿 메서드를 통해 SavepointManager 구현체에 접근하여 savepoint 해제
@Override
public void releaseSavepoint(Object savepoint) throws TransactionException {
  getSavepointManager().releaseSavepoint(savepoint);
}

// SavepointManager 반환 템플릿 메서드
protected SavepointManager getSavepointManager() {
  throw new NestedTransactionNotSupportedException("This transaction does not support savepoints");
}
```

## DefaultTransactionStatus

트랜잭션의 상태와 동작을 관리하는 TransactionStatus의 기본 구현체로 트랜잭션을 나타내는 객체임

상속 관계

```text
TransactionExecution, SavepointManager, Flushable,
-\ TransactionStatus
--\ AbstractTransactionStatus
---\ DefaultTransactionStatus 
```

포함하고 있는 정보
- [AbstractPlatformTransactionManager](./AbstractPlatformTransactionManager.md)가 필요로 하는 모든 정보
- [PlatformTransactionManager](#platformtransactionmanager) 구현체에서 구현한 트랜잭션 객체(Object 타입)

사용처
- [AbstractPlatformTransactionManager](./AbstractPlatformTransactionManager.md) 내부
- 그 이외의 다른 곳에서 사용 X, 테스트가 필요한 경우 [`SimpleTransactionStatus`](#simpletransactionstatus) 사용

### DefaultTransactionStatus - 필드

```java
@Nullable
private final String transactionName;

// 트랜잭션 매니저 구현체가 실제로 사용하는 구체적인 트랜잭션 객체(트랜잭션 리소스/컨텍스트)를 참조하는 필드
@Nullable
private final Object transaction;

// 기존 트랜잭션에 참여하지 않고, 새 트랜잭션인지
private final boolean newTransaction;

// 해당 트랜잭션에 대해 새 트랜잭션 동기화가 열린건지 
private final boolean newSynchronization;

// 중첩된 트랜잭션인지
private final boolean nested;

private final boolean readOnly;

// debug 모드 로깅용
private final boolean debug;

// 이 트랜잭션에 대해 중지된 리소스를 보관하는 필드
@Nullable
private final Object suspendedResources;
```

위의 필드에 대한 조회 메서드는 생략함(hasTransaction 제외)

```java
@Override
public boolean hasTransaction() {
  return (this.transaction != null);
}
```

### DefaultTransactionStatus - 메서드

DefaultTransactionStatus는 트랜잭션 매니저 구현체(JpaTransactionManager 등)가 구현한 트랜잭션 객체를 참조하는 `Object transaction` 필드를 가짐

이 필드를 기반으로 구현하는 메서드들은 다음과 같음

#### 글로벌 트랜잭션 롤백 설정 확인

SmartTransactionObject는 트랜잭션 객체가 롤백 전용 상태인지 확인할 수 있는 인터페이스로 하이버네이트, JDBC, JPA 등의 트랜잭션 객체가 구현함

```java
@Override
public boolean isGlobalRollbackOnly() {
    return (this.transaction instanceof SmartTransactionObject smartTransactionObject &&
            smartTransactionObject.isRollbackOnly());
}
```

#### SavepointManager 확인 및 반환

```java
public boolean isTransactionSavepointManager() {
    return (this.transaction instanceof SavepointManager);
}
```

트랜잭션 객체가 SavepointManager 타입이 아닌 경우 예외 발생

```java
@Override
protected SavepointManager getSavepointManager() {
    Object transaction = this.transaction;
    if (!(transaction instanceof SavepointManager savepointManager)) {
        throw new NestedTransactionNotSupportedException(
                "Transaction object [" + this.transaction + "] does not support savepoints");
    }
    return savepointManager;
}
```

#### flush

DefaultTransactionStatus가 상속 관계에 따라 간접적으로 확장하는 TransactionStatus는 Flushable 인터페이스를 구현함

flush 메서드는 세션을 기반으로 데이터베이스에 모든 데이터를 SmartTransactionObject를 통해 내보냄

```java
@Override
public void flush() {
    if (this.transaction instanceof SmartTransactionObject smartTransactionObject) {
        smartTransactionObject.flush();
    }
}
```

## SimpleTransactionStatus

PlatformTransactionManager 커스텀 구현체 또는 테스트 용도로 사용하는 TransactionStatus 구현체

```java
public class SimpleTransactionStatus extends AbstractTransactionStatus {

	private final boolean newTransaction;

	public SimpleTransactionStatus() {
		this(true);
	}

	public SimpleTransactionStatus(boolean newTransaction) {
		this.newTransaction = newTransaction;
	}

	@Override
	public boolean isNewTransaction() {
		return this.newTransaction;
	}
}
```