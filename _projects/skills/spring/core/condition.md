---
layout: default
title:
---

[Spring Condition](#spring-condition)

[Spring ConfigurationCondition](#spring-configurationcondition)

[Spring Boot condition](../../boot/txt/condition.md)


## Spring Condition

특정 조건에 따라 빈을 등록하거나 무시하기 위해 사용되는 스프링에서 제공하는 메커니즘으로

조건 로직을 구현한 Condition 객체와 해당 로직을 적용할 대상을 지정하는 @Conditional 어노테이션으로 구성됨

크게 세 가지 정보를 기반으로 빈의 등록 여부를 결정함

1. 프로퍼티
2. 클래스 패스
3. 평가할 클래스에 대한 메타데이터

프로퍼티에 지정된 속성이나 클래스 패스에 존재하는 라이브러리의 여부에 따라 빈의 등록 여부를 결정하곤 함

Condition은 조건에 부합하는지 true/false를 반환하는 메서드만을 가지고 있는 함수형 인터페이스임

true 반환 시: 추가적인 평가를 진행하거나 빈 등록

false 반환 시: 빈으로 등록하지 않으며, 해당 타입/메서드에 선언된 다른 어노테이션들(@Import 등)도 적용되지 않음

```java
@FunctionalInterface
public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

    // ConditionContext: 현재 애플리케이션 컨텍스트와 관련된 정보(Environment, ResourceLoader, ClassLoader 등)를 제공하는 객체
    // AnnotatedTypeMetadata: 평가할 클래스나 메서드에 대한 메타데이터를 제공하는 객체
}
```

@Conditional은 Condition 타입의 클래스 배열을 파라미터로 가지며, 적용된 Condition의 반환 값에 따라 빈 등록 처리를 진행하는 어노테이션임

주로 @Component, @Bean, @Configuration에 선언함

애플리케이션 로드 시점에 @Conditional이 적용된 클래스나 메서드에 대해서 Condition 구현체가 동작함

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

    Class<? extends Condition>[] value();
    
}
```

#### 예시(@Profile)

@Profile은 아래와 같이 현재 애플리케이션에 활성화된 Profile 값이 포함된 경우 빈을 등록하는 어노테이션으로

`@Conditional(ProfileCondition.class)`이 적용된 합성 어노테이션임

```java
@Component
@Profile("prod")
public class UserService {
    
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {
    
    String[] value();

}
```

ProfileCondition은 해당 클래스나 메서드에 적용된 Profile을 가져온 뒤, 현재 Environment의 설정된 Profile에 포함되는 경우에만 true를 반환함

즉, @Profile은 프로퍼티 정보를 기반으로 빈 등록을 결정하는 @Conditional 어노테이션임

```java
class ProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().matchesProfiles((String[]) value)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
```

[간단한 예제](../src/main/java/com/hansanhha/spring/core/condition/SimpleConfiguration.java)

## Spring ConfigurationCondition

스프링이 애플리케이션 컨텍스트를 설정하는 과정 중 특정 구성 단계(configuration phase)에서 condition을 적용하고자 할 때 사용하는 인터페이스임

조건이 평가될 수 있는 시점은 다음과 같음
- PARSE_CONFIGURATION: @Configuration 클래스가 파싱되는 시점에 조건 평가, 이 시점에 매치되지 않으면 컨텍스트에 추가되지 않음
- REGISTER_BEAN: @Configuration 클래스를 제외한 다른 방법으로 빈이 추가되는 시점에 조건 평가, @Configuration 클래스가 스프링 빈으로 등록된 이후의 시점

```java
public interface ConfigurationCondition extends Condition {

    ConfigurationPhase getConfigurationPhase();
    
    enum ConfigurationPhase {
        
        PARSE_CONFIGURATION,
        
        REGISTER_BEAN
    }
}
```