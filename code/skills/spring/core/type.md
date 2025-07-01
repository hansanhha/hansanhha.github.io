---
layout: default
title:
---

[ClassMetadata](#classmetadata)

[AnnotatedTypeMetadata](#annotatedtypemetadata)

[AnnotationMetadata](#annotationmetadata)

## ClassMetadata

특정 클래스에 대한 메타데이터를 제공하는 인터페이스임
- 클래스 이름, 패키지, 상속 관계 등의 구조적 정보

주로 빈 정의, 조건 평가, 컨텍스트 초기화 과정에서 메타데이터를 활용하기 위해 사용됨

```java
public interface ClassMetadata {
    
    // 클래스 전체 이름(패키지 포함)
    String getClassName();
    
    boolean isInterface();
    
    boolean isAnnotation();
    
    boolean isAbstract();
    
    boolean isFinal();
    
    // 중첩 클래스인지 확인
    boolean isIndependent();
    
    // 구현된 인터페이스 이름 배열 반환
    String[] getInterfaceNames();
    
    // 멤버 클래스 이름 배열 반환
    String[] getMemberClassNames();
    
    // default 메서드 생략
}
```

## AnnotatedTypeMetadata

AnnotattedTypeMetadata는 클래스나 메서드, 필드에 적용된 어노테이션 메타데이터를 제공하는 인터페이스

어노테이션 정보를 기반으로 조건을 평가하거나, 빈 등록할 때 활용함

[MergedAnnotations](./annotation.md/#mergedannotations)를 반환하는 메서드를 가짐

**AnnotatedTypeMetadata 인터페이스**
```java
public interface AnnotatedTypeMetadata {
    
    MergedAnnotations getAnnotations();
    
    // default, static 메서드 생략
}
```

## AnnotationMetadata

특정 클래스에 적용된 어노테이션 메타데이터를 제공하는 인터페이스

클래스의 구조적 정보를 제공하는 ClassMetadata와 어노테이션 메타데이터를 제공하는 AnnotatedTypeMetadata 인터페이스를 확장함 

```java
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

    // 주어진 어노테이션 타입이 적용된 모든 메서드 반환
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);
    
    // 클래스에 선언된 모든 메서드 반환
    Set<MethodMetadata> getDeclaredMethods();
    
    // default, static 메서드 생략
}
```

