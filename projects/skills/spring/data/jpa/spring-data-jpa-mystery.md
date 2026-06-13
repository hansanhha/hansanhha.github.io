---
layout: default
title:
---

[Spring Data JPA Mystery](#spring-data-jpa-mystery)

[Spring Data JPA는 인터페이스를 선언하는 것만으로 어떻게 DB 작업 수행을 가능하게 할까](#spring-data-jpa는-인터페이스를-선언하는-것만으로-어떻게-db-작업-수행을-가능하게-할까)

[@Query, 메서드명 기반 쿼리는 어떻게 실행되는 걸까](#query-메서드명-기반-쿼리는-어떻게-실행되는-걸까)

[하나의 EntityManager가 어떻게 여러 스레드에서 안전하게 동작할까](#하나의-entitymanager가-어떻게-여러-스레드에서-안전하게-동작할까)

[스프링 데이터 JPA 리포지토리 인터페이스 실제 동작 과정 정리](#스프링-데이터-jpa-리포지토리-인터페이스-실제-동작-과정-정리)

[Spring Data JPA Objects](#spring-data-jpa-objects)

## Spring Data JPA Mystery

스프링 데이터 JPA는 개발자가 데이터베이스 작업이 무료하다고 느끼게 할만큼 편리한 기능을 제공한다

대부분의 데이터베이스 구성 코드를 캡슐화한만큼 비즈니스 로직에 더욱 집중할 수 있지만 이를 동작케하는 내면에서 이뤄지는 일련의 과정이 궁금하지 않을 수 없다

그래서 실제로 애플리케이션을 만들면서 의문이 들었던 점 중 가장 궁금증을 일으킨 3가지를 꼽아보았다

이 문서는 각 궁금증에 대한 해답과 동작 과정에 참여하는 객체들을 알아볼 것이다

## Spring Data JPA는 인터페이스를 선언하는 것만으로 어떻게 DB 작업 수행을 가능하게 할까

스프링 데이터 JPA 자동 구성 과정
- 스프링 부트 자동 구성 활성화 [JpaRepositoriesAutoConfiguration](../txt/spring%20data%20jpa%20autoconfiguration.md#jparepositoriesautoconfiguration)
- 리포지토리 인터페이스 프록시 구현체 스프링 빈 등록 및 SimpleJpaRepository 인스턴스 생성
- 리포지토리 인터페이스 프록시 구현체 주입
- 리포지토리 인터페이스 메서드 호출 -> 프록시 구현체 위임 -> SimpleJpaRepository 등 실행 위임 -> EntityManager DB 작업 수행

스프링 부트에 의해 스프링 데이터 JPA 리포지토리 스캔과 등록 기능이 트리거되면서 스프링 데이터 JPA가 리포지토리 인터페이스를 동적으로

프록시 패턴을 사용한 구현체를 스프링 빈으로 등록하면 스프링 컨텍스트에 의해 해당 타입으로 의존성 주입됨에 따라 DB 작업의 수행이 가능해진다

그렇다면 리포지토리 인터페이스의 프록시 구현체는 누가, 어떻게 생성하는 걸까?

### 스프링 데이터 JPA의 리포지토리 인터페이스 프록시 구현체 생성 과정

스프링 데이터는 JPA 뿐만 아니라 다른 데이터 모듈들도 지원하므로 리포지토리 스캔 및 프록시 구현체 생성 과정을 추상화한다

[스프링 데이터의 프록시 구현체 생성 과정](../../common/spring%20data%20repository%20proxy%20creation%20process.md)을 보고 온 다음 스프링 데이터 JPA와 관련된 부분들을 살펴보자



스프링 데이터 JPA 리포지토리 인터페이스 관련 자동 구성은 [`JpaRepositoriesAutoConfiguration`](./spring%20data%20jpa%20autoconfiguration.md#jparepositoriesautoconfiguration) 클래스에 의해 시작되며

실질적으로 리포지토리 인터페이스 스캔, 프록시 생성 및 스프링 빈 등록 과정은 [JpaRepositoriesRegistrar](#jparepositoriesregistrar-스프링-부트)에 의해 트리거된다


SimpleJpaRepository는 사용자가 선언한 리포지토리 인터페이스의 메서드를 구현하고 있기에 위임만 하면 손쉽게 요청을 처리할 수 있다

하지만 리포지토리 인터페이스 메서드가 아닌 사용자가 직접 만든 `findByUserName` 메서드나 `@Query("SELECT ...")` 같은 쿼리 메서드는 SimpleJpaRepository가 해결해 줄 수 없다

프록시 구현체는 이러한 영속화 작업을 어떻게 처리할 수 있는걸까?

## @Query, 메서드명 기반 쿼리는 어떻게 실행되는 걸까




## 하나의 EntityManager가 어떻게 여러 스레드에서 안전하게 동작할까



## 스프링 데이터 JPA 리포지토리 인터페이스 실제 동작 과정 정리

#### 1. 리포지토리 인터페이스 정의

Repository 인터페이스를 확장한 리포지토리 인터페이스 정의

```java
  public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastName(String lastName);
  }
```

#### 2. 스프링 부트 자동 구성, 프록시 패턴 적용

스프링 부트 자동 구성 활성화 [HibernateJpaAutoConfiguration](../txt/spring%20data%20jpa%20autoconfiguration.md#hibernatejpaautoconfiguration), [JpaRepositoriesAutoConfiguration](../txt/spring%20data%20jpa%20autoconfiguration.md#jparepositoriesautoconfiguration)

1. 모든 리포지토리 인터페이스 스캔(`@EnableJpaRepositories` 어노테이션이 설정된 패키지)
2. 스캔된 각 리포지토리 인터페이스마다 **프록시 구현체** 생성 및 스프링 빈 등록
3. [JpaRepositoryFactory](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/support/JpaRepositoryFactory.html)를 통해 각각의 엔티티에 대한 리포지토리 인터페이스 기본 구현체 SimpleJpaRepository 생성

예시
- 사용자가 정의한 ProductRepository, OrderRepository 리포지토리 인터페이스에 대한 프록시 구현체 생성
- JpaRepositoryFactory -> 각각의 엔티티(Product, Order)에 대해 SimpleJpaRepository 인스턴스 생성
- ProductRepositoryProxy -> `SimpleJpaRepository<Product, Long>` 인스턴스 호출 위임
- OrderRepositoryProxy -> `SimpleJpaRepository<Order, Long>` 인스턴스 호출 위임

#### 프록시 구현체 동작 방식

JpaRepositoryFactory에 의해 생성된 리포지토리 인터페이스 프록시 구현체는 **쿼리의 종류**에 따라 **적절한 구현체로 위임**하는 역할을 수행함

todo 수정 필요

- 기본 CRUD 메서드: SimpleJpaRepository에게 위임 -> SimpleJpaRepository는 EntityManager를 통해 DB 작업 수행
- 메서드명 기반 쿼리(메서드명 파싱을 통해 동적으로 JPQL 쿼리 생성): `JpaQueryMethodFactory`를 통해 메서드명 기반 쿼리 파악 ->  `PartTree`를 통해 키워드 분석 후 JPQL 생성 -> EntityManager를 통해 DB 작업 수행
- 사용자 정의 쿼리(`@Query`): `JpaQueryMethodFactory`를 통해 커스텀 `@Query` 기반 쿼리 파악 -> `@Query` 내용을 EntityManager에게 전달하여 DB 작업 수행, 메서드 파라미터를 `@Query`에 바인딩
- 커스텀 구현체의 메서드 호출: 해당 구현체에게 위임

#### 3. 의존성 주입

사용자가 선언한 리포지토리 인터페이스 타입으로 JpaRepositoryFactory에 의해 생성된 프록시 구현체 주입

```java
@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  // 스프링 부트 자동 구성에 의해 생성된 UserRepository 타입의 프록시 객체 주입
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
```

### JpaRepositoryFactory

JpaRepositoryFactory는 사용자가 정의한 리포지토리 인터페이스를 구현한 프록시 객체를 생성하는 역할을 가짐

스프링 데이터 프로젝트는 JPA 이외에도 다양한 하위 모듈을 지원하고 있으며 일반적으로 중복 로직이 발생하는 부분은 추상화하여 공통 처리함

리포지토리 인터페이스 프록시 구현체 생성 로직 또한 다른 스프링 데이터 모듈에서도 동일하게 진행되므로 

스프링은 [RepositoryFactorySupport](../../common/RepositoryFactorySupport.md) 를 통해 리포지토리 인터페이스 프록시 구현체 생성 공통 로직을 추상화함 

JpaRepositoryFactory는 RepositoryFactorySupport를 상속하여 Jpa에 특화된 리포지토리 인터페이스 프록시 구현체를 생성함

jparepositorybean

## Spring Data JPA Objects

### LocalContainerEntityManagerFactoryBean

### SharedEntityManagerBean

### JpaRepositoriesRegistrar (스프링 부트)

스프링 부트의 자동 구성에 의해 활성화되는 스프링 데이터 JPA 리포지토리 인터페이스 스캔 및 리포지토리 인터페이스 스프링 컨텍스트 등록의 시작점이다

JpaRepositoriesRegistrar는 `RepositoryConfigurationDelegate.registerRepositoriesIn` 메서드를 호출하여 리포지토리 스캔 및 등록 과정을 수행한다

JpaRepositoriesRegistrar 상세 과정
- JpaRepositoriesRegistrar 등록
  - JpaRepositoriesAutoConfiguration는 JpaRepositoriesImportSelector를 @Import함 (@Import 클래스는 스프링 빈이 아니라 BeanDefinition 메타데이터를 등록하는 단계에서 등록됨)
  - JpaRepositoriesImportSelector는 동적으로 스프링 컨텍스트에 등록할 수 있는 `ImportSelector` 인터페이스를 확장함
  - `JpaRepositoriesImportSelector.determineImport` 메서드를 통해 JpaRepositoriesRegistrar를 스프링 컨텍스트에 등록함
- JpaRepositoriesRegistrar 실행
  - JpaRepositoriesRegistrar는 AbstractRepositoryConfigurationSourceSupport를 상속함
  - AbstractRepositoryConfigurationSourceSupport는 동적으로 스프링 빈을 등록할 수 있는 `ImportBeanDefinitionRegistrar` 인터페이스를 확장함
  - `AbstractRepositoryConfigurationSourceSupport.registerBeanDefinitions` 메서드에서 `RepositoryConfigurationDelegate.registerRepositoriesIn` 메서드를 호출하여 리포지토리 스캔 및 등록 과정을 수행함

