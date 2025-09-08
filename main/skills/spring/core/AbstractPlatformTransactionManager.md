---
layout: default
title:
---

[AbstractPlatformTransactionManager](#abstractplatformtransactionmanager)

[상속 관계](#상속-관계)

[필드](#필드)

[트랜잭션 시작: getTransaction](#트랜잭션-시작-gettransaction)
- [getTransaction 메서드 내부에서 호출하는 메서드](#gettransaction-메서드-내부에서-호출하는-메서드)
  - [트랜잭션 생성/참여 및 트랜잭션 동기화 처리](#트랜잭션-생성참여-및-트랜잭션-동기화-처리)
    - [handleExistingTransaction](#handleexistingtransaction)
    - [startTransaction](#starttransaction)
    - [newTransactionStatus](#newtransactionstatus)
    - [prepareTransactionStatus](#preparetransactionstatus)
    - [prepareSynchronization](#preparesynchronization)
    - [suspend](#suspend)
  - [트랜잭션 시작 위임: doBegin](#트랜잭션-시작-위임-dobegin)

[트랜잭션 커밋: commit](#트랜잭션-커밋-commit)
- [processCommit](#processcommit)
- [doCommit](#docommit)

[트랜잭션 롤백: rollback](#트랜잭션-롤백-rollback)
- [processRollback](#processrollback)
- [doRollback](#dorollback)

## AbstractPlatformTransactionManager

스프링은 최상위 인터페이스를 정의한 뒤, 구현체를 만들기 전에 구현체들이 공통적으로 사용될 로직들을 추상 클래스에 중앙화하고 세부적인 구현을 템플릿 메서드에 맡기는 코드 구조를 즐겨 사용한다

AbstractPlatformTransactionManager 역시 이러한 코드 구조를 가지는 트랜잭션 매니저 구현체의 기반 클래스로, 스프링의 표준 트랜잭션 동작 흐름들을 중앙화하고 구현체 별로 처리가 다른 부분은 추상 메서드를 정의한다

공통 로직 처리
- 트랜잭션 관리(시작/커밋/롤백)
- 트랜잭션 동기화 관리
- 동기화 콜백 리스너 트리거

## 상속 관계

```txt
TransactionManager
-\ PlatformTransactionManager, ConfigurableTransactionManager
--\ AbstractPlatformTransactionManager
```

TransactionManager: 스프링 데이터 트랜잭션 매니저 마커 인터페이스

PlatformTransactionManager: 트랜잭션 관리 추상화

ConfigurableTransactionManager: TransactionExecutionListener 관리
- TransactionExecutionListener: 트랜잭션 생성/진행 시 호출하는 콜백 인터페이스

## 필드

```java
// 트랜잭션 동기화 상수
public static final int SYNCHRONIZATION_ALWAYS = 0;
public static final int SYNCHRONIZATION_ON_ACTUAL_TRANSACTION = 1;
public static final int SYNCHRONIZATION_NEVER = 2;

// 트랜잭션 동기화 상수 Map
static final Map<String, Integer> constants = Map.of(
        "SYNCHRONIZATION_ALWAYS", SYNCHRONIZATION_ALWAYS,
        "SYNCHRONIZATION_ON_ACTUAL_TRANSACTION", SYNCHRONIZATION_ON_ACTUAL_TRANSACTION,
        "SYNCHRONIZATION_NEVER", SYNCHRONIZATION_NEVER
);

// 트랜잭션 동기화 설정
private int transactionSynchronization = SYNCHRONIZATION_ALWAYS;

// 트랜잭션 최대 지속 시간 (TransactionDefinition.TIMEOUT_DEFAULT = -1)
private int defaultTimeout = TransactionDefinition.TIMEOUT_DEFAULT;

// 트랜잭션 중첩 허용 여부
private boolean nestedTransactionAllowed = false;

// 기존 트랜잭션 검증 여부
private boolean validateExistingTransaction = false;

// 트랜잭션 실패 시 전체 트랜잭션 롤백 실행 여부
private boolean globalRollbackOnParticipationFailure = true;

// globalRollbackOnly 상태로 설정된 경우 커밋 시도 전 예외 발생 여부
private boolean failEarlyOnGlobalRollbackOnly = false;

// 커밋 실패 시 롤백 여부
private boolean rollbackOnCommitFailure = false;

// transactionExecutionListener 관리
private Collection<TransactionExecutionListener> transactionExecutionListeners = new ArrayList<>();
```

## 트랜잭션 시작: getTransaction

친절하게 PlatformTransactionManager의 구현부라는 것을 주석으로 표시해준 옛날 스프링 코드의 모습

```java
//---------------------------------------------------------------------
// Implementation of PlatformTransactionManager
//---------------------------------------------------------------------
```

getTransaction 메서드는 트랜잭션 전파 설정에 따라 트랜잭션을 생성, 참여하고 TransactionStatus 객체를 반환함

[TransactionDefinition](./transaction.md#transactiondefinition): 트랜잭션 설정 정보 보관 객체(이 정보를 바탕으로 트랜잭션 생성)
- 트랜잭션 이름, 트랜잭션 전파, 격리 수준, 시간 초과, 읽기 전용

```java
@Override
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
        throws TransactionException {
    
    // 주어진 TransactionDefinition 또는 기본(모든 값 설정 X) 사용
    TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());

    // doGetTransaction 추상 메서드를 통해 구현체(JpaTransactionManager 등)로부터 트랜잭션 객체 획득 
    Object transaction = doGetTransaction();

    /*
        doGetTransaction 메서드에서 획득한 트랜잭션이 이미 활성화된 트랜잭션인지 확인
        
        기존 트랜잭션이 있는 경우라면 handleExistingTransaction 메서드 실행 
        - 파라미터로 받은 TransactionDefinition의 트랜잭션 전파 설정 값에 따른 트랜잭션 처리 후 결과(TransactionStatus) 반환
     */
    if (isExistingTransaction(transaction)) {
        return handleExistingTransaction(def, transaction, debugEnabled);
    }

    /*
        위의 조건문에 해당되지 않는 경우는 doGetTransaction()에서 반환한 트랜잭션이
        새로운 트랜잭션인 것으로 간주할 수 있다
        
        아래의 로직은 파라미터로 받은 TransactionDefinition의
        트랜잭션 전파 설정 값에 따른 트랜잭션 처리 후 결과(TransactionStatus)를 반환한다
     */
  
  
    // 트랜잭션 설정 값(트랜잭션 최대 지속 시간) 검증
    if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
        throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
    }

    // TransactionDefinition.PROPAGATION_MANDATORY 설정 값은 기존 트랜잭션을 필수로 요구한다(없으면 예외 발생)
    // 이 부분까지 로직이 도달한 경우 기존 트랜잭션이 없는 것이므로 예외 발생
    if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
        throw new IllegalTransactionStateException(
                "No existing transaction found for transaction marked with propagation 'mandatory'");
    }
    
    // REQUIRED, REQUIRES_NEW, NESTED 설정 값들인 경우 새 트랜잭션 생성 또는 기존 트랜잭션에 참여한다
    else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        
        // 현재 활성화된 트랜잭션 동기화 리소스들이 있는 경우 중단시킨다
        SuspendedResourcesHolder suspendedResources = suspend(null);
        
        try {
            // 새로운 트랜잭션 시작 후 결과(TransactionStatus)를 반환한다
            return startTransaction(def, transaction, false, debugEnabled, suspendedResources);
        }
        catch (RuntimeException | Error ex) {
            // startTransaction 메서드에서 예외 발생 시, 기존 트랜잭션 재개 후 예외를 다시 던진다
            resume(null, suspendedResources);
            throw ex;
        }
    }
    else {
        // 위에 포함되지 않는 기타 트랜잭션 전파 설정 값(SUPPORT, NOT_SUPPORT, NEVER)인 경우 비어있는 트랜잭션을 생성한다
        // 실제 트랜잭션은 아니지만, 잠재적으로 트랜잭션 동기화될 수 있다
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        return prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
    }
    
}
```

## getTransaction 메서드 내부에서 호출하는 메서드

## 트랜잭션 생성/참여 및 트랜잭션 동기화 처리

[getTransaction](#트랜잭션-시작-gettransaction) 메서드 내부 또는 간접적으로 호출되는 메서드들
- [handleExistingTransaction](#handleexistingtransaction): 기존 트랜잭션 처리
- [startTransaction](#starttransaction): 트랜잭션 생성 및 시작, 동기화 준비
- [newTransactionStatus](#newtransactionstatus): 트랜잭션 생성(TransactionStatus 생성)
- [prepareTransactionStatus](#preparetransactionstatus): 트랜잭션 생성 및 동기화 준비
- [prepareSynchronization](#preparesynchronization): 트랜잭션 동기화 준비
- [suspend](#suspend): 트랜잭션 및 트랜잭션 동기화 중지

### handleExistingTransaction

getTransaction 메서드에서 트랜잭션 생성을 처리하기 전에 기존 트랜잭션이 있는지 확인하는데, 있는 경우 이 메서드를 호출함

handleExistingTransaction 메서드는 트랜잭션 전파 설정 값에 따른 트랜잭션 생성/참여, 트랜잭션 동기화 설정 처리를 진행하여 TransactionStatus 객체 생성함

#### 메서드 파라미터

- `TransactionDefinition`: `getTransaction(TransactionDefinition)`에서 전달받은 파라미터 (트랜잭션 설정 정보)
- `Object`: `getTransaction` 메서드에서 가져온 트랜잭션 매니저 구현체의 트랜잭션 객체, 기존 트랜잭션
- `boolean`: logger 설정 값

```java
private TransactionStatus handleExistingTransaction(
			TransactionDefinition definition, Object transaction, boolean debugEnabled)

```

**트랜잭션 전파 설정 값에 따른 분기 처리**
- PROPAGATION_NEVER
    - TransactionDefinition.PROPAGATION_NEVER 설정은 트랜잭션을 필요로 하지 않음 - 예외 발생
    ```java
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
      throw new IllegalTransactionStateException(
          "Existing transaction found for transaction marked with propagation 'never'");
    }
    ```

- PROPAGATION_NOT_SUPPORTED
    - TransactionDefinition.PROPAGATION_NOT_SUPPORTED는 기존 트랜잭션이 있는 경우 기존 트랜잭션을 보류하고, 트랜잭션 없이 작업을 바로 수행함
    ```java
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
        // 기존 트랜잭션 중지
        Object suspendedResources = suspend(transaction);
  
        // 트랜잭션 동기화 설정 값 확인
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
  
        // 트랜잭션 없이 시작
        return prepareTransactionStatus(definition, null, false, newSynchronization, debugEnabled, suspendedResources);
    }
    ```

- PROPAGATION_NOT_SUPPORTED
    - TransactionDefinition.PROPAGATION_REQUIRES_NEW는 기존 트랜잭션이 있는 경우 트랜잭션에 참여하고, 없는 경우 트랜잭션 없이 수행함
    ```java
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
        SuspendedResourcesHolder suspendedResources = suspend(transaction);
        try {
            return startTransaction(definition, transaction, false, debugEnabled, suspendedResources);
        }
        catch (RuntimeException | Error beginEx) {
            resumeAfterBeginException(transaction, suspendedResources, beginEx);
            throw beginEx;
        }
    }
    ```

- PROPAGATION_NESTED
    - TransactionDefinition.PROPAGATION_NESTED는 기존 트랜잭션이 없으면 새 트랜잭션을 만들고, 있으면 SavePoint를 이용하여 중첩 트랜잭션을 생성함
    - 중첩 트랜잭션을 지원하지 않는 트랜잭션 매니저 구현체라면 NestedTransactionNotSupportedException 발생
    ```java
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        if (!isNestedTransactionAllowed()) {
            throw new NestedTransactionNotSupportedException(
                    "Transaction manager does not allow nested transactions by default - " +
                    "specify 'nestedTransactionAllowed' property with value 'true'");
        }
        
        /*
            useSavepointForNestedTransaction()의 기본 반환 값: true
            
            SavePoint 기능을 사용하는 트랜잭션 매니저 구현체인 경우
            스프링이 관리하고 있는 트랜잭션 내에서 SavePointManager API를 구현한 TransactionStatus를 통해 saveppoint를 생성함
            일반적으로 JDBC savepoint 사용하며, 스프링 동기화를 활성화하지 않음
         */
        if (useSavepointForNestedTransaction()) {
            DefaultTransactionStatus status = newTransactionStatus(
                    definition, transaction, false, false, true, debugEnabled, null);
            this.transactionExecutionListeners.forEach(listener -> listener.beforeBegin(status));
            try {
                status.createAndHoldSavepoint();
            }
            catch (RuntimeException | Error ex) {
                this.transactionExecutionListeners.forEach(listener -> listener.afterBegin(status, ex));
                throw ex;
            }
            this.transactionExecutionListeners.forEach(listener -> listener.afterBegin(status, null));
            return status;
        }
        
        /*
           SavePoint 기능을 사용하지 않는 트랜잭션 매니저 구현체인 경우
           SavePoint 없이 중첩된 begin, commit/rollback 호출을 통한 중첩 트랜잭션 생성
           일반적으로 JTA에만 해당되며 기존 JTA 트랜잭션의 경우 여기서 스프링 동기화가 활성화될 수 있음
         */
        else {
            return startTransaction(definition, transaction, true, debugEnabled, null);
        }
    }
    ```

- PROPAGATION_REQUIRED, PROPAGATION_SUPPORTS, PROPAGATION_MANDATORY
    - 기존 트랜잭션의 검증이 필요한 트랜잭션 매니저 구현체의 경우 두 가지를 확인함
        - 기존 트랜잭션의 격리 수준과 새로 생성할 트랜잭션의 격리 수준이 다른지
        - 기존 트랜잭션의 읽기 전용 설정 값과 새로 생성할 트랜잭션의 읽기 전용 설정 값이 다른지
    - 트랜잭션 매니저 구현체의 트랜잭션 매니저 동기화 설정 값에 따라 트랜잭션 생성
    ```java 
  
    /*
        기존 트랜잭션 검증이 필요한 경우(isValidateExistingTransaction() 기본값: false)
        트랜잭션 격리 수준 및 읽기 전용 설정 값 일치 여부 확인
    */
    if (isValidateExistingTransaction()) {
      if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
          Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
          if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
              throw new IllegalTransactionStateException("Participating transaction with definition [" +
                      definition + "] specifies isolation level which is incompatible with existing transaction: " +
                      (currentIsolationLevel != null ?
                              DefaultTransactionDefinition.getIsolationLevelName(currentIsolationLevel) :
                              "(unknown)"));
          }
      }
      if (!definition.isReadOnly()) {
          if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
              throw new IllegalTransactionStateException("Participating transaction with definition [" +
                      definition + "] is not marked as read-only but existing transaction is");
          }
      }
    }
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
  
    // DefaultTransactionStatus 생성 및 트랜잭션 동기화 준비
    return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
    ```

### startTransaction

[getTransaction](#트랜잭션-시작-gettransaction) 또는 [handleExistingTransaction](#handleexistingtransaction) 메서드에서 새 TransactionStatus 인스턴스를 생성하고자 할 때 호출하는 메서드

단순 생성 뿐만 아니라 콜백 리스너 호출, 트랜잭션 동기화 준비 처리까지 수행함

동작 흐름
- 트랜잭션 매니저 구현체의 트랜잭션 동기화 설정 값에 따라 새 트랜잭션 생성 [newTransactionStatus](#newtransactionstatus)
- 트랜잭션 시작 전처리
- 트랜잭션 시작
- 트랜잭션 시작 후처리 [preparationSynchronization](#preparesynchronization)

호출되는 경우
- 기존 트랜잭션이 없을 때: `PROPAGATION_REQUIRED` `PROPAGATION_REQUIRES_NEW` `PROPAGATION_NESTED` 트랜잭션 전파인 경우
- 기존 트랜잭션이 있을 때: `PROPAGATION_REQUIRES_NEW` 트랜잭션 전파인 경우

```java
private TransactionStatus startTransaction(TransactionDefinition definition, Object transaction,
			boolean nested, boolean debugEnabled, @Nullable SuspendedResourcesHolder suspendedResources) {

    // 트랜잭션 동기화 설정을 바탕으로 새 트랜잭션 동기화 필요 여부 확인 
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    
    // newTransaction 메서드 호출 - DefaultTransactionStatus 생성
    DefaultTransactionStatus status = newTransactionStatus(
            definition, transaction, true, newSynchronization, nested, debugEnabled, suspendedResources);
    
    // 트랜잭션 시작 전 콜백 리스너 호출
    this.transactionExecutionListeners.forEach(listener -> listener.beforeBegin(status));
    
    // 트랜잭션 시작
    try {
        doBegin(transaction, definition);
    }
    
    // 예외 발생 시 콜백 리스너 호출(예외 전달) 후 예외 재던짐
    catch (RuntimeException | Error ex) {
        this.transactionExecutionListeners.forEach(listener -> listener.afterBegin(status, ex));
        throw ex;
    }
    
    // 트랜잭션 동기화 준비
    prepareSynchronization(status, definition);
    
    // 트랜잭션 시작 후 콜백 리스너 호출
    this.transactionExecutionListeners.forEach(listener -> listener.afterBegin(status, null));
    return status;
}
```

### newTransactionStatus

AbstractPlatformTransactionManager에서 DefaultTransactionStatus를 생성하는 유일한 메서드

메서드 파라미터
- definition: 생성할 TransactionStatus 인스턴스에 대한 트랜잭션 정보
- transaction: [getTransaction](#트랜잭션-시작-gettransaction) 메서드에서 획득한 트랜잭션 매니저 구현체의 트랜잭션 객체(신규 트랜잭션, 기존 트랜잭션 또는 없는 경우(null))
- newTransaction: 신규 트랜잭션 여부
- newSynchronization: 새 트랜잭션 동기화 여부
- nested: 중첩 트랜잭션 여부
- debug: logger 설정값
- suspendedResources: 중지된 트랜잭션의 리소스(해당 되는 경우에만)

```java
private DefaultTransactionStatus newTransactionStatus(
			TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction,
			boolean newSynchronization, boolean nested, boolean debug, @Nullable Object suspendedResources) {

    // 진짜 새로운 트랜잭션 동기화가 필요한지 확인
    // newSynchronization 값이 true이면서 기존 트랜잭션 동기화가 중지되지 않은 경우(!TransactionSynchronizationManager.isSynchronizationActive())
    boolean actualNewSynchronization = newSynchronization &&
              !TransactionSynchronizationManager.isSynchronizationActive();
    
    // DefaultTransactionStatus 생성 밎 반환
    return new DefaultTransactionStatus(definition.getName(), transaction, newTransaction,
              actualNewSynchronization, nested, definition.isReadOnly(), debug, suspendedResources);

```

### prepareTransactionStatus

[newTransactionStatus](#newtransactionstatus) 호출 - TransactionStatus 생성

[prepareSynchronization](#preparesynchronization) 호출 - 트랜잭션 동기화 준비

```java
private DefaultTransactionStatus prepareTransactionStatus(
			TransactionDefinition definition, @Nullable Object transaction, boolean newTransaction,
			boolean newSynchronization, boolean debug, @Nullable Object suspendedResources) {

    DefaultTransactionStatus status = newTransactionStatus(
            definition, transaction, newTransaction, newSynchronization, false, debug, suspendedResources);
    prepareSynchronization(status, definition);
    return status;
}
```

### prepareSynchronization

트랜잭션 상태 정보 DefaultTransactionStatus를 통해 새 트랜잭션 동기화가 필요하다고 판단되면 트랜잭션 동기화 매니저를 통해 트랜잭션 동기화 설정을 수행함

트랜잭션 동기화 설정 목록
- 트랜잭션 활성화 여부(setActualTransactionActive)
- 트랜잭션 격리 수준(setCurrentIsolationLevel)
- 트랜잭션 읽기 전용 여부(setCurrentTransactionReadOnly)
- 트랜잭션 이름(setCurrentTransactionName)
- 트랜잭션 동기화 초기화(initSynchronization)

```java
protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
    if (status.isNewSynchronization()) {
        TransactionSynchronizationManager.setActualTransactionActive(status.hasTransaction());
        TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(
                definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT ?
                        definition.getIsolationLevel() : null);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(definition.isReadOnly());
        TransactionSynchronizationManager.setCurrentTransactionName(definition.getName());
        TransactionSynchronizationManager.initSynchronization();
    }
}
```

### suspend

**동작**
- 파라미터로 주어진 트랜잭션에 대한 트랜잭션 동기화를 중지하고, `doSuspend` 템플릿 메서드를 통해 자식 구현체에게 트랜잭션 중지를 위임함
- 파라미터에 null을 건넨 경우 활성화된 트랜잭션 동기화만 중지시킴(동기화가 활성화된 경우)

**반환 값** 
- 중지된 동기화 리소스 정보
- 트랜잭션과 트랜잭션 동기화 모두 활성화되지 않은 경우 null 반환

트랜잭션 동기화 매니저의 동기화 활성화 상태와 파라미터 값에 따라 분기 처리한다

```java
@Nullable
protected final SuspendedResourcesHolder suspend(@Nullable Object transaction) throws TransactionException {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {...}
    else if (transaction != null) {...}
    else {...}
}
```

#### 트랜잭션 동기화가 활성화된 경우

동작
- 리소스 중지: 활성화된 트랜잭션 동기화, 트랜잭션 객체(null이 아닌 경우)
- 반환값 생성: 중지된 트랜잭션(nullable) 및 트랜잭션 동기화 객체 정보 리스트, 현재 스레드의 트랜잭션 동기화 설정값

```java
if (TransactionSynchronizationManager.isSynchronizationActive()) {
      
    // private 메서드 doSuspendSynchronization 호출
    // 모든 동기화 중지, 현재 스레드에 대한 트랜잭션 동기화 비활성화 
    // 중지된 트랜잭션 동기화 정보 ransactionSynchronization 객체 리스트 반환
    List<TransactionSynchronization> suspendedSynchronizations = doSuspendSynchronization();
      
    try {
        Object suspendedResources = null;
          
        // 파라미터로 받은 트랜잭션 객체가 null이 아닌 경우 템플릿 메서드 doSuspend 호출하여 트랜잭션 중지
        if (transaction != null) {
            suspendedResources = doSuspend(transaction);
        }
          
        // 현재 트랜잭션 동기화 매니저에 설정된 값을 추출하고 기본 값으로 되돌림
        String name = TransactionSynchronizationManager.getCurrentTransactionName();
        TransactionSynchronizationManager.setCurrentTransactionName(null);
          
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
          
        Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(null);
          
        boolean wasActive = TransactionSynchronizationManager.isActualTransactionActive();
        TransactionSynchronizationManager.setActualTransactionActive(false);
          
        // 중지된 트랜잭션(nullable), 트랜잭션 동기화 객체 리스트, 트랜잭션 동기화 설정 값을 기반으로 반환 값 생성
        return new SuspendedResourcesHolder(
                suspendedResources, suspendedSynchronizations, name, readOnly, isolationLevel, wasActive);
    }
    catch (RuntimeException | Error ex) {
          
        // 템플릿 메서드 doSuspend 메서드 실패 시 중지한 트랜잭션 리소스 재개 후 예외 재던짐
        doResumeSynchronization(suspendedSynchronizations);
        throw ex;
    }
}
```

suspend 메서드에서 호출하는 doSuspendSynchronization 메서드는 모든 트랜잭션 동기화 중지 및 트랜잭션 동기화 스레드를 비우고, 

중지된 트랜잭션 동기화 TransactionSynchronization 객체 리스트를 반환함

```java
private List<TransactionSynchronization> doSuspendSynchronization() {
      
    // 트랜잭션 동기화 매니저로부터 현재 스레드의 동기화 객체를 모두 가져옴
    List<TransactionSynchronization> suspendedSynchronizations =
            TransactionSynchronizationManager.getSynchronizations();
      
    // 동기화 중지 
    for (TransactionSynchronization synchronization : suspendedSynchronizations) {
        synchronization.suspend();
    }

    // 동기화 객체 삭제 및 중지된 동기화 객체 반환
    TransactionSynchronizationManager.clearSynchronization();
    return suspendedSynchronizations;
}
```
    
#### 트랜잭션 동기화가 활성화되지 않았지만 주어진 트랜잭션이 null이 아닌 경우

doSuspend 템플릿 메서드를 호출하여 트랜잭션 매니저 구현체에게 트랜잭션 중지 위임

중지된 트랜잭션 정보를 기반으로 SuspendedResourcesHolder 반환

```java
else if (transaction != null) {
    Object suspendedResources = doSuspend(transaction);
    return new SuspendedResourcesHolder(suspendedResources);
}
```

#### 모두 해당되지 않는 경우

아무것도 수행하지 않고 null 반환

```java
else {
    return null;
}
```

## 트랜잭션 시작 위임: doBegin

```java
protected abstract void doBegin(Object transaction, TransactionDefinition definition)
			throws TransactionException;
```

AbstractPlatformTransactionManager는 추상 메서드를 사용해서 시작할 트랜잭션 객체와 해당 트랜잭션에 대한 정보를 전달해서 트랜잭션 매니저 구현체에게 트랜잭션 시작을 위임한다

이 메서드는 [startTransaction](#starttransaction) 메서드 내부에서 호출되며 트랜잭션 매니저가 시작할 새 트랜잭션을 결정하고 기존 트랜잭션이 없거나 기존 트랜잭션을 중지한 상태이다 

또한 AbstractPlatformTransactionManager가 [getTransaction](#트랜잭션-시작-gettransaction) 메서드에서 이미 트랜잭션 전파를 처리했기 때문에 트랜잭션 매니저 구현체에서 별도로 트랜잭션 전파 행동에 관련한 처리를 적용하지 않아도 된다

#### Savepoint 기능을 사용할 때 주의점

`AbstractPlatformTransactionManager.useSavepointForNestedTransaction` 메서드의 기본 반환값은 true이지만

구현체의 오버라이딩으로 false 값을 반환한다면 savepoint를 사용하지 않음

이 경우 중첩 트랜잭션에 대한 구분점이 없게 되므로 doBegin 메서드는 **중첩 트랜잭션의 시작을 명시적으로 처리해야 됨**

이미 활성화된 트랜잭션이 있는 경우 이를 적절히 감지하고 새 트랜잭션을 시작할 수 있어야 함

## 트랜잭션 커밋: commit

파라미터로 받은 TransactionStatus를 통해 롤백 상태이면 롤백을 수행하고(`processRollback`) 아니라면 실제 커밋을 수행하는 `processCommit` 추상 메서드를 호출한다

```java
@Override
public final void commit(TransactionStatus status) throws TransactionException {
    
    // 이미 완료된 트랜잭션인 경우 IllegalTransactionStateException 발생
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    // DefaultTransactionStatus 다운 캐스팅
    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    
    //  현재 트랜잭션 상태가 롤백 설정인 경우(지역 롤백 전용) 롤백 수행
    if (defStatus.isLocalRollbackOnly()) {
        processRollback(defStatus, false);
        return;
    }

    //  글로벌 롤백 상태이면서 글로벌 롤백 전용 상태에 커밋을 하지 않아야 하는 경우 롤백 수행
    if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
        processRollback(defStatus, true);
        return;
    }

    // 실제 커밋을 수행할 메서드 위임
    processCommit(defStatus);
}
```

### processCommit

실질적으로 커밋 작업을 수행하는 메서드로 [commit](#트랜잭션-커밋-commit) 메서드에서 트랜잭션이 완료되지 않았고 롤백 전용 상태가 아닌 경우 호출한다

다만 commit 메서드에서 글로벌 롤백 전용 상태를 정확히 감지하지 못할 수 있기 때문에 이 메서드에서 한 번 더 검증하며 글로벌 롤백 전용 활성화를 감지한 경우 `UnexpectedRollbackException`을 발생시킨다  

#### 주요 동작

1. 커밋, 트랜잭션 완료 전처리
2. 트랜잭션 종류(savepoint, 신규 트랜잭션, 글로벌 트랜잭션 조기 실패 설정)에 따른 트랜잭션 처리(커밋 수행 등)
3. 커밋 후처리
4. 트랜잭션 완료 후처리

코드는 크게 트랜잭션 커밋 전, 후 트리거/콜백 리스너 호출 코드와 글로벌 롤백 설정 관련 코드, 예외 처리 코드로 이루어져 있으나

트랜잭션 커밋 과정에서 발생할 수 있는 예외나 꼭 수행해야 할 작업들이 있다보니 중첩된 try문이 작성되면서 되게 복잡한 것처럼 보임

```java
private void processCommit(DefaultTransactionStatus status) throws TransactionException {
    try {
        // 커밋 전처리 메서드 호출 여부
        boolean beforeCompletionInvoked = false;
        
        // 트랜잭션 커밋 콜백 리스너 호출 여부
        boolean commitListenerInvoked = false;

        /* --------------------------------------------------------------
           트랜잭션 커밋 수행
           - 트랜잭션 전처리
           - 트랜잭션 상태에 따른 트랜잭션 처리 메서드 위임(releaseHeldSavepoint, doCommit)
           - 글로벌 롤백 설정 감지 및 상황에 따른 UnexpectedRollbackException 발생
         -------------------------------------------------------------- */
        try {
            // 예상치 못한 글로벌 롤백 전용 설정 활성화 여부
            boolean unexpectedRollback = false;
            
            // 커밋 전 트랜잭션, 트랜잭션 동기화 전처리
            prepareForCommit(status);
            triggerBeforeCommit(status);
            triggerBeforeCompletion(status);
            beforeCompletionInvoked = true;

            // 현재 트랜잭션이 savepoint를 가진 경우
            // 글로벌 롤백 활성화 여부 확인, 트랜잭션 커밋 콜백 리스너 호출, savepoint 해제(롤백 X)
            if (status.hasSavepoint()) {
                unexpectedRollback = status.isGlobalRollbackOnly();
                this.transactionExecutionListeners.forEach(listener -> listener.beforeCommit(status));
                commitListenerInvoked = true;
                status.releaseHeldSavepoint();
            }
            
            // 현재 트랜잭션이 savepoint를 가지지 않으면서 신규 트랜잭션인 경우
            // 글로벌 롤백 활성화 여부 확인, 트랜잭션 커밋 콜백 리스너 호출, doCommit(템플릿 메서드) 호출
            else if (status.isNewTransaction()) {
                unexpectedRollback = status.isGlobalRollbackOnly();
                this.transactionExecutionListeners.forEach(listener -> listener.beforeCommit(status));
                commitListenerInvoked = true;
                doCommit(status);
            }
            
            // 현재 트랜잭션이 savepoint를 가지지 않으면서, 신규 트랜잭션도 아닌데
            // 글로벌 롤백 상태에서 조기 실패 설정 값이 참인 경우에
            // 글로벌 롤백 활성화 여부를 확인한다
            else if (isFailEarlyOnGlobalRollbackOnly()) {
                unexpectedRollback = status.isGlobalRollbackOnly();
            }

            // 위의 조건문을 통해 글로벌 롤백 활성화를 감지한 경우 UnexpectedRollbackException를 던진다
            if (unexpectedRollback) {
                throw new UnexpectedRollbackException(
                        "Transaction silently rolled back because it has been marked as rollback-only");
            }
        }
        
        /* --------------------------------------------------------------------
           트랜잭션 처리 위임 메서드에서 발생한 예외 및 UnexpectedRollbackException 처리
         ---------------------------------------------------------------------- */
        
        // 위의 로직에서 글로벌 롤백 설정 활성화 감지하여 UnexpectedRollbackException이 발생된 경우 
        // afterCompletion 콜백 트리거, 트랜잭션 커밋 콜백 리스너 호출, 예외 재던짐 
        catch (UnexpectedRollbackException ex) {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
            this.transactionExecutionListeners.forEach(listener -> listener.afterRollback(status, null));
            throw ex;
        }
        
        // 트랜잭션 처리 위임 메서드(doCommit)에서 커밋 처리에 실패하여 TransactionException이 발생된 경우
        // 롤백 전용 활성화가 되어있으면 롤백 수행 후 예외 재던짐
        // 안되어있으면 afterCompletion 콜백 트리거, 트랜잭션 커밋 콜백 리스너 호출, 예외 재던짐
        catch (TransactionException ex) {
            if (isRollbackOnCommitFailure()) { // isRollbackOnCommitFailure() 기본값: false
                doRollbackOnCommitException(status, ex);
            }
            else {
                triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
                if (commitListenerInvoked) {
                    this.transactionExecutionListeners.forEach(listener -> listener.afterCommit(status, ex));
                }
            }
            throw ex;
        }
        
        // 위에 해당되지 않는 언체크, 체크 예외 발생 시
        // 트랜잭션 전처리 과정에서 예외가 발생한 경우 beforeCompletion 콜백 트리거
        // 롤백 수행
        catch (RuntimeException | Error ex) {
            if (!beforeCompletionInvoked) {
                triggerBeforeCompletion(status);
            }
            doRollbackOnCommitException(status, ex);
            throw ex;
        }
        
        /* --------------------------------------------------------------------
                트랜잭션 커밋 후처리 (정상적으로 커밋 처리가 된 시점)
         ---------------------------------------------------------------------- */

        // afterCommit 콜백 트리거, 트리거 내에서 예외가 발생하더라도 커밋된 것으로 간주한다
        try {
            triggerAfterCommit(status);
        }
        
        // afterCompletion 콜백 트리거, 트랜잭션 커밋 콜백 리스너 호출
        finally {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_COMMITTED);
            if (commitListenerInvoked) {
                this.transactionExecutionListeners.forEach(listener -> listener.afterCommit(status, null));
            }
        }

    }
    
    // 트랜잭션 완료 후처리
    finally {
        cleanupAfterCompletion(status);
    }
}
```

### doCommit

트랜잭션 매니저 구현체에 실질적인 커밋 처리를 위임하는 추상 메서드

[processCommit](#processcommit) 메서드에서 새로운 트랜잭션인 경우 호출한다

```java
protected abstract void doCommit(DefaultTransactionStatus status) throws TransactionException;
```

## 트랜잭션 롤백: rollback

트랜잭션이 완료된 경우 IllegalTransactionStateException를 던지고, 아니라면 processRollback 메서드 호출

```java
@Override
public final void rollback(TransactionStatus status) throws TransactionException {

    // 이미 완료된 트랜잭션인 경우 IllegalTransactionStateException 발생
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    // DefaultTransactionStatus 다운 캐스팅
    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    
    // processRollback 호출
    processRollback(defStatus, false);
}
```

### processRollback

실질적으로 롤백 작업을 수행하는 메서드로 [commit](#트랜잭션-커밋-commit)과 [rollback](#트랜잭션-롤백-rollback) 메서드에서 호출됨

#### 두 번째 파라미터 unexpected

commit또는 [processCommit](#processcommit) 메서드에서 트랜잭션의 롤백 상태를 감지했을 때, 예상하지 못한 롤백 상황을 나타내는 플래그이다

트랜잭션이 정상적으로 커밋될 것으로 기대했으나 실제로는 롤백이 필요한 상태로 전환된 경우에 설정된다

#### 주요 동작

1. 트랜잭션 완료 전처리
2. 트랜잭션 종류(savepoint, 신규 트랜잭션, 중첩 트랜잭션 또는 트랜잭션 없음)에 따른 트랜잭션 처리(롤백 수행 등)
3. 롤백 후처리
4. 트랜잭션 완료 후처리

```java
private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
    try {
        
        // 예상치 못한 글로벌 롤백 전용 설정 활성화 여부
        boolean unexpectedRollback = unexpected;

        // 트랜잭션 롤백 콜백 리스너 호출 여부
        boolean rollbackListenerInvoked = false;

        /* --------------------------------------------------------------
           트랜잭션 롤백 수행
           - 트랜잭션 전처리
           - 트랜잭션 상태에 따른 트랜잭션 처리 메서드 위임(rollbackToHeldSavepoint, doRollback, 롤백 설정)
         -------------------------------------------------------------- */        
        
        try {
            // 트랜잭션 완료 전 트리거 호출
            triggerBeforeCompletion(status);

            // 현재 트랜잭션이 중첩 트랜잭션이면서 savepoint를 가진 경우
            // 트랜잭션 롤백 콜백 리스너 호출, savepoint로 롤백 후 savepoint 해제
            if (status.hasSavepoint()) {
                this.transactionExecutionListeners.forEach(listener -> listener.beforeRollback(status));
                rollbackListenerInvoked = true;
                status.rollbackToHeldSavepoint();
            }
            
            // 현재 트랜잭션이 신규 트랜잭션인 경우
            // 트랜잭션 롤백 콜백 리스너 호출, doRollback(템플릿 메서드) 호출
            else if (status.isNewTransaction()) {
                this.transactionExecutionListeners.forEach(listener -> listener.beforeRollback(status));
                rollbackListenerInvoked = true;
                doRollback(status);
            }
            
            // 신규 트랜잭션이 아니면서 상위 트랜잭션에 참여했으나 savepoint가 없거나
            // 트랜잭션 자체가 없는 경우(PROPAGATION_NEVER 등)
            else {
                
                // 현재 트랜잭션이 상위 트랜잭션에 참여한 경우
                // 롤백 전용이 활성화된 경우 현재 트랜잭션을 롤백 전용으로 설정
                // 아닌 경우 상위 트랜잭션 관리자에게 롤백 여부 결정을 하도록 냅둠
                if (status.hasTransaction()) {
                    if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
                        doSetRollbackOnly(status);
                    }
                    else {
                    }
                }
                
                // 트랜잭션이 없는 경우 아무것도 하지 않음
                else {
                }
                
                // 글로벌 롤백 전용 상태에서 조기 실패가 설정되지 않은 경우 unexpectedRollback false 처리
                if (!isFailEarlyOnGlobalRollbackOnly()) {
                    unexpectedRollback = false;
                }
            }
        }
        
        /* --------------------------------------------------------------
           언체크드, 체크드 예외 처리
           
           트랜잭션 완료 트리거, 트랜잭션 롤백 콜백 메서드 호출
           예외 재던짐
         -------------------------------------------------------------- */
        catch (RuntimeException | Error ex) {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            if (rollbackListenerInvoked) {
                this.transactionExecutionListeners.forEach(listener -> listener.afterRollback(status, ex));
            }
            throw ex;
        }
        
        /* --------------------------------------------------------------
           트랜잭션 롤백 후처리 (정상적으로 롤백 처리가 된 시점)
         -------------------------------------------------------------- */

        triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
        if (rollbackListenerInvoked) {
            this.transactionExecutionListeners.forEach(listener -> listener.afterRollback(status, null));
        }

        // unexpectedRollback 플래그가 참인 경우 UnexpectedRollbackException 발생
        // commit 수행 과정에서 글로벌 롤백 전용 설정이 활성화된 경우 unexpectedRollback을 참으로 설정하고 롤백 수행 
        if (unexpectedRollback) {
            throw new UnexpectedRollbackException(
                    "Transaction rolled back because it has been marked as rollback-only");
        }
    }
    
    // 트랜잭션 완료 후처리
    finally {
        cleanupAfterCompletion(status);
    }
}
```

### doRollback

트랜잭션 매니저 구현체에 실질적인 롤백 처리를 위임하는 추상 메서드

[doCommit](#docommit) 처리 중 예외가 발생하거나 [processRollback](#processrollback)에서 새로운 트랜잭션인 경우 호출한다

```java
protected abstract void doRollback(DefaultTransactionStatus status) throws TransactionException;
```