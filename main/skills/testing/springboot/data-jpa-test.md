---
layout: default
title:
---

[data jpa test](#data-jpa-test)

[@Transactional](#transactional)

[실제 데이터베이스 대상 테스트 수행](#실제-데이터베이스-대상-테스트-수행)

[테스트 코드](../src/test/java/hansanhha/slice/DataJpaSliceTest.java)

## data jpa test

@DataJpaTest는 @Entity 클래스와 스프링 data jpa 리포지토리를 구성한다

또한 클래스패스에 임베디드 데이터베이스가 활성화된 경우에만 임베디드 데이터베이스를 자동 구성하며 JPA 표준 EntityManager 대신 테스트용 엔티티 매니저 TestEntityManager를 구성한다

showSql 속성의 기본값이 true이기 때문에 자동적으로 sql 쿼리를 로깅한다


## @Transactional

@DataJpaTest 어노테이션에 @Transactional이 메타 어노테이션으로 적용되어 있어서 각 테스트를 마치고 나면 기본적으로 롤백을 수행한다

이 기능을 비활성화하려면 테스트 클래스 또는 테스트 메서드에 @Transactional(propatation = Propagation.NOT_SUPPORTED)를 적용해야 한다


## 실제 데이터베이스 대상 테스트 수행

인메모리 임베디드 데이터베이스 대신 실제 데이터베이스를 대상으로 테스트를 수행하려면 다음 어노테이션을 추가한다

```java
@DataJpaTest
// 실제 데이터베이스를 대상으로 테스트를 수행한다
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DataJpaSliceTest {
    
}
```