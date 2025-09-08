---
layout: default
title:
---

[Spring Data Repository Magic](#spring-data-repository-magic)

[Proxy Creation Related Objects](#proxy-creation-related-objects)

## Spring Data Repository Magic

스프링 데이터의 가장 강력한 기능 중 한 가지는 리포지토리 인터페이스만 정의해도 실제로 DB 작업을 수행할 수 있게 하는 것이다

애플리케이션 로드 시점 또는 그 이후의 시점에 내부적으로 어떤 마법이 발생해서 우리를 편하게 해주는데, 마법은 어느 스프링 데이터 하위 모듈을 사용하더라도 적용된다

이 문서는 인터페이스 리포지토리 프록시 구현체 생성 과정을 살펴보면서 마법의 실체를 파헤쳐본다

내용 구성
- 주요 참여 객체
- 전체 과정
- 요약

## Proxy Creation Related Objects

스프링 데이터 인터페이스 리포지토리 프록시 구현체 생성 과정에 참여하는 주요 객체를 각 목적에 따라 분류했다

Bean 글자만 제외하면 동일한 이름을 가진 클래스들이 있다
- 클래스 이름에 "Bean"이 들어가는 경우: 스프링 컨테이너 상호작용하는 클래스 (FactoryBean, BeanSupport 등)
- 클래스 이름에 "Bean"이 들어가지 않는 경우: 내부 로직(기술적인 로직)을 담당하는 클래스 (Factory, Support 등)

#### 프록시 구현체 생성 관련

*RepositoriesRegistrar 
- 리포지토리 인터페이스 자동 구성 시작점 (ImportBeanDefinitionRegistrar를 구현하여 리포지토리의 설정 정보를 읽고 등록 과정 진입점 역할 수행)
- 스프링 데이터 JPA: [JpaRepositoriesRegistrar](../jpa/txt/spring%20data%20jpa%20mystery.md#jparepositoriesregistrar-스프링-부트)

[RepositoryConfigurationDelegate](./spring%20data%20repository%20config%20objects.md#repositoryconfigurationdelegate)
- [RepositoryConfiguration](./spring%20data%20repository%20config%20objects.md#repositoryconfiguration), [RepositoryConfigurationSource](./spring%20data%20repository%20config%20objects.md#repositoryconfigurationsource)를 기반으로 리포지토리 인터페이스 스캔 및 스프링 컨텍스트 등록

[RepositoryBeanDefinitionBuilder]()
- 리포지토리 인터페이스의 BeanDefinition을 생성하기 위한 BeanDefinitionBuilder 인스턴스 생성

[RepositoryFactoryBeanSupport]()
- 다양한 데이터 스토어(JPA, MongoDB) 리포지토리 팩토리 빈을 위한 공통 기반 클래스
- 리포지토리 팩토리에게 프록시 생성을 위임하고 프록시를 스프링 빈으로 등록한다
- 스프링 데이터 JPA: [JpaRepositoryFactoryBean](../jpa/txt/spring%20data%20jpa%20objects.md#jparepositoryfactorybean)

[RepositoryFactorySupport]()
- 리포지토리 프록시 생성 관련 공통 로직 처리
- 프록시 생성 로직, 메서드 이름 기반 쿼리 생성 등 모든 데이터 스토어의 공통 기반 클래스.

*RepositoryFactory
- 특정 데이터 스토어 별 리포지토리 프록시 생성 담당
- 스프링 데이터 JPA: [JpaRepositoryFactory](../jpa/txt/spring%20data%20jpa%20objects.md#jparepositoryfactory)

#### 리포지토리 정보 추상화

[RepositoryConfiguration]()

[RepositoryConfigurationSource](./spring%20data%20repository%20source.md#repositoryconfigurationsource)
- @EnableJpaRepositories에 정의된 속성(basePackages, repositoryBaseClass 등)을 [RepositoryConfigurationDelegate](./spring%20data%20objects.md#repositoryconfigurationdelegate)에서 사용할 수 있도록 제공함

#### 데이터 모듈(스토어)별 리포지토리 설정 확장 처리

[RepositoryConfigurationExtension](./spring%20data%20objects.md#repositoryconfigurationextension)
- 특정 데이터 스토어(JPA, MongoDB 등)에 필요한 리포지토리 설정 확장 처리
- 스프링 데이터 JPA: JPA 전용 추가 설정, [SimpleJpaRepository](../jpa/txt/simple%20jpa%20repository.md) 기본 구현체 설정

#### 프록시 구현체 동작 관련 객체

QueryLookupStrategy