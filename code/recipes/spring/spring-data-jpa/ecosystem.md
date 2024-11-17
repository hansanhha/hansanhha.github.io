---
layout: default
title: code/recipes/spring/spring data jpa/ecosystem
---

### 영속화

디스크에 있는 프로그램을 실행하면 메모리에 적재되어 프로세스로 동작함

그동안 사용되는 데이터는 메모리 내에서만 존재하고 프로세스가 종료되면 데이터도 같이 사라짐

애플리케이션을 구동하려면 영구적으로 데이터를 보관해야 되는데, 이 때 필요한 작업이 **영속화**임

자바가 아니더라도 데이터를 어딘가 보관해야 하는 애플리케이션이라면 영속화는 **필수불가결한 작업**으로

파일 시스템에 읽고 쓰거나 데이터베이스에 저장할 수 있음

### 화성에서 온 프로그래밍 언어, 금성에서 온 관계형 데이터베이스

프로그램을 만드는 프로그래밍 언어와 데이터를 저장하는 관계형 데이터베이스는 본질적으로 서로 다른 목적을 가진 도구이기 때문에 **데이터**를 취급하는 방식이 상이함 

1. 데이터 표현 방식

자바는 필드와 메서드를 가진 객체로 데이터를 표현하고, 인스턴스화하여 메모리에 적재함

관계형 데이터베이스는 테이블을 사용해서 데이터를 표현하고, 디스크에 행과 열로 저장함

2. 상속

OOP 언어는 상속, 확장을 통해 코드 중앙화 및 재사용성을 높일 수 있지만

관계형 데이터베이스는 상속 개념이 없음

3. 관계

OOP에서 객체 사이의 관계를 연관(Association), 합성(Composition), 집합(Aggregation) 관계로 표현하는 것과 달리

관계형 데이터베이스에서는 테이블 간 관계를 외래 키나 조인으로 해결함

4. 식별

메모리에서 동작하는 프로그래밍 언어는 참조를 통해 데이터를 식별하고

관계형 데이터베이스는 기본 키를 사용하여 테이블의 특정 열을 식별함

### ORM

객체지향 언어와 관계형 데이터베이스 간의 패러다임 불일치 해결을 위한 도구를 ORM(Object-Relational Mapping)이라고 함

자바-스프링 환경에서 일반적으로 사용되는 기술스택은 다음과 같음

**JPA(Java Persistence API)**: ORM 기능을 추상화한 자바 표준 명세

**Hibernate**: JPA 구현 프레임워크

**Querydsl**: 타입 안전한 동적 쿼리를 작성할 수 있는 DSL(Domain Specific Language)

**Spring Data JPA**: 스프링 환경에서 JPA를 쉽게 사용할 수 있도록 제공하는 Spring Data 프로젝트 하위 모듈

### Spring Data

스프링 데이터 프로젝트는 데이터 저장소(Data Source)의 특수성을 유지하면서 데이터 접근을 위한 일관성 있는 스프링 기반 프로그래밍 모델을 제공하여

관계형 및 비관계형 데이터베이스, 클라우드 기반 데이터 서비스, 맵 리듀스 프레임워크 등을 쉽게 사용할 수 있게 함

스프링 데이터는 특정 데이터베이스에 특화된 하위 모듈을 포함하는 포괄적인 프로젝트로 스프링 환경의 데이터 접근을 추상화하고, 각 모듈에서 특성에 맞는 구현을 제공함

#### 주요 하위 모듈

Spring Data JPA: JPA 기반 ORM 데이터베이스 접근 간소화

Spring Data MongoDB: NoSQL 데이터베이스인 MongoDB에 대한 접근 간소화

Spring Data Redis: 인메모리 데이터 저장소인 Redis에 대한 지원

Spring Data Elasticsearch: 검색 엔진 Eleasticsearch에 대한 데이터 접근 지원

Spring Data JDBC: 단순한 SQL 데이터베이스 접근을 위한 지원

이외에도 Spring Data Cassandra, Couchbase, Neo4j 같은 하위 모듈이 존재함

#### 주요 기능

**리포지토리 패턴 및 커스텀 객체(데이터 표현) 추상화**
- 기본적인 CRUD 작업을 처리해주는 추상화 제공

**메서드 이름 기반 동적 쿼리 생성**
- JPQL 또는 SQL을 직접 작성할 필요없이 메서드 이름을 기반으로 쿼리를 자동 생성함

**베이스 클래스**
- 도메인 클래스의 반복적인 코드를 줄여주는 베이스 클래스 

**auditing 지원**
- 엔티티 생성 및 수정 시간 등을 자동으로 기록하는 감사 기능 제공

**커스텀 리포지토리 코드 통합**
- 커스텀 리포지토리를 만들어 기존 리포지토리에 통합

**JavaConfig**
- 자바 코드 설정을 통해 데이터 저장소 및 트랜잭션 매니저 등을 설정

#### Spring Data 주요 추상화

**Repository**

스프링 데이터 프로젝트의 최상위 인터페이스로, 모든 하위 모듈에서 일관된 방식으로 영속성 계층을 처리하기 위한 기초 인터페이스

```java
@Indexed
public interface Repository {
}
```

**CrudRepository**

기본적인 CRUD 작업을 정의한 인터페이스

```java
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    // 기본 CRUD 메서드
}
```

**PagingAndSortingRepository**

페이징과 정렬 기능을 추가로 제공하는 인터페이스

```java
@NoRepositoryBean
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {

    Iterable<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
}
```
