---
layout: default
title:
---

## RepositoryFactorySupport

스프링 데이터 모듈은 리포지토리 인터페이스를 선언하는 것만으로 DB 작업을 가능하게 한다

스프링 데이터가 `@EnableJpaRepositories`를 통해 감지한 리포지토리 인터페이스와 관련한 인스턴스들을 만들어주기 때문인데, 그 역할을 RepositoryFactorySupport가 수행한다

주어진 리포지토리 인터페이스에 대한 **프록시 구현체**를 생성하고 [QueryExecutorMethodInterceptor](./QueryExecutorMethodInterceptor.md)에게 제어(쿼리 실행)를 전달하는 어드바이스를 적용하여 리포지토리 인터페이스 메서드 호출을 적절히 처리한다

또한 해당 리포지토리 인터페이스에 명시된 엔티티를 대상으로 하는 **SimpleJpaRepository 인스턴스**를 생성하여 프록시 구현체에게 주입하고 프록시의 위임 대상으로 사용한다

이후 생성된 프록시 구현체는 스프링 컨텍스트에 등록된다

## RepositoryFactoryBeanSupport

### TransactionalRepositoryFactoryBeanSupport



## QueryLookupStrategy

## RepositoryQuery

## QueryExecutorMethodInterceptor


## RepositoryComposition

## RepositoryInvokerFactory

### DefaultRepositoryInvokerFactory





## ParameterAccessor

## EvaluationContextProvider

