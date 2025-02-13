index
- [annotation](#annotation)
- [java.lang.annotation.Annotation](#javalangannotationannotation)
- [annotation vs interface](#annotation-vs-interface)
- [how annotation works](#how-annotation-works)
- [meta annotation](#meta-annotation)
- [compose annotation](#compose-annotation)
- [reflect annotation](#reflect-annotation)
- [@Target](#target)
- [@Retention](#retention)


## annotation

자바의 어노테이션은 프로그램 코드에 메타데이터를 제공하여 코드에 영향을 미치거나 런타임 시 읽혀서 동작한다 

주석(comment)과 달리 단순히 정보 전달 목적으로만 사용되지 않고 컴파일러에게 정보를 전달하거나 런타임에 특정 동작을 트리거하는 용도로 사용된다

아래와 같이 @interface 키워드를 사용하여 어노테이션을 정의할 수 있다 

```java
public @interface SimpleAnnotation {
} 
```

그리고 어노테이션 바디에 **속성**을 추가하거나 다른 어노테이션을 [메타 어노테이션](#meta-annotation)으로 적용할 수 있다  

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

    String value() default "default value"; // 선택 속성 (기본값: default value)
    
    int number(); // 필수 속성
            
}
```

일반적으로 어노테이션은 클래스처럼 **멤버**를 선언하는 것 뿐만 아니라 **속성(attribute)** 또는 **요소(element)** 이라는 것을 정의할 수 있다

속성은 어노테이션의 메타데이터를 정의하고 컴파일러 또는 런타임에 동작 방식을 유도하는 중요한 요소로, 메서드처럼 반환 타입과 속성 이름 옆에 소괄호를 붙여서 정의한다

default 키워드가 붙은 속성은 **선택 속성**으로 선언 주체가 별도의 값을 지정하지 않으면 기본 값이 적용되고, 붙지 않은 속성은 **필수 속성**으로 항상 값을 지정해야 한다

속성에 대한 자세한 규칙은 다음과 같다
- 매개변수 없는 메서드 형태로 정의한다
- 접근 제한자(`public abstract`)는 암묵적으로 적용되며 생략할 수 있다
- 메서드와 달리 중괄호가 아닌 세미콜론으로 끝난다
- 기본값은 **상수**만 가능하며 null을 사용할 수 없다
- 속성이 단 한개이면서 이름이 value일 때는 선언 주체가 속성 이름을 생략하고 값을 지정할 수 있다
- **선언 주체는 속성의 값을 동적으로 정의할 수 없으며 반드시 컴파일 타임에 값을 결정해야 한다**

또한 바이트코드에서의 표현과 직렬화(serialization) 문제로 인해 어노테이션에서는 아래의 타입만 제한적으로 허용된다

```java
public @interface AlmostAttributeAnnotation {

    // 어노테이션에서 선언할 수 있는 타입
    // primitive, String, Class, Enum, Annotation 및 허용 가능한 타입의 배열
    int number();
    String text();
    Class<?> clazz();
    MyEnum enumValue();
    MyAnnotation annotation();
    int[] numbers();

    enum MyEnum{
    }
}
```


## java.lang.annotation.Annotation

모든 어노테이션은 암묵적으로 java.lang.annotation.Annotation 인터페이스를 상속한다

이를 통해 바이트코드에서 어노테이션은 특수한 인터페이스 구현체로 표현된다

```java
public interface Annotation {
    
    boolean equals(Object obj);

    boolean equals(Object obj);

    String toString();
    
    Class<? extends Annotation> annotationType();
}
```


## annotation vs interface

어노테이션은 @interface 키워드를 사용해 정의되며, [공식 문서](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.6)에서 annotation interfaces라고 설명되는데 둘은 무슨 연관 관계가 있을까?

자바 언어 사양에 따르면 어노테이션은 [Annotation](#javalangannotationannotation)을 암묵적으로 상속하며 interface 키워드에 @만 붙인 형태로 컴파일러가 인식하는 특수한 인터페이스다

어노테이션과 인터페이스는 다음과 같은 공통점을 가진다
- public abstract 메서드만 정의할 수 있다 (어노테이션의 요소는 암묵적으로 public abstract 속성이 적용된다)
- 인스턴스화할 수 없다

그리고 다음과 같은 차이점을 가진다
- 정의 키워드: @interface vs interface
- 목적: 메타 데이터 제공 vs 다형성을 위한 계약 정의
- 동작 방식: 리플렉션 vs 상속
- 상속: java.lang.annotation.Annotation vs Object (직계 상속 없음)
- 메서드 성격: public abstract (기본값 제공 가능) vs public abstract
- 리플렉션: getAnnotation()으로 직접 읽기 가능 vs 리플렉션으로 메서드 호출


## how annotation works

어노테이션의 메타데이터는 클래스와 인터페이스와 마찬가지로 jvm의 method area에 저장된다

즉, 컴파일 시 어노테이션에 대한 클래스 파일이 생성된 후 jvm method area로 로드된다 (Class<MyAnnotation> 객체가 생성되며, 속성 정보를 포함한 어노테이션 정적 메타데이터가 저장됨)

어노테이션을 선언한 주체(클래스, 메서드, 필드 등)가 속성 값을 정의하면 해당 정보는 바이트코드의 RuntimeVisibleAnnotations 또는 RuntimeInvisibleAnnotations 섹션에 저장된다

또한 어노테이션은 컴파일 타임에 바이트코드로 고정되는 불변 데이터로 속성 값을 동적으로 정의할 수 없다 (method area에 저장되는 메타데이터는 전부 불변으로 설계되었으며 어노테이션은 컴파일 타임에 바이트코드로 저장, 런타임에 상수로 참조된다) 

[MyAnnotation](https://github.com/hansanhha/hansanhha.github.io/blob/default/code/stack/java/types/source%20code/src/main/java/hansanhha/annotations/MyAnnotation.java)을 컴파일한 후 `javap -v MyAnnotation.class` 명령을 사용하면 아래와 같은 결과가 출력된다

```text
Last modified Feb 13, 2025; size 510 bytes
  SHA-256 checksum 8e9f828d8457eb908263724c666f395d9d7131accb82353fabbada3cf0bcff29
  Compiled from "MyAnnotation.java"
public interface hansanhha.annotations.MyAnnotation extends java.lang.annotation.Annotation
  minor version: 0
  major version: 65
  flags: (0x2601) ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT, ACC_ANNOTATION
  this_class: #1                          // hansanhha/annotations/MyAnnotation
  super_class: #3                         // java/lang/Object
  interfaces: 1, fields: 0, methods: 2, attributes: 2
Constant pool:
   #1 = Class              #2             // hansanhha/annotations/MyAnnotation
   #2 = Utf8               hansanhha/annotations/MyAnnotation
   #3 = Class              #4             // java/lang/Object
   #4 = Utf8               java/lang/Object
   #5 = Class              #6             // java/lang/annotation/Annotation
   #6 = Utf8               java/lang/annotation/Annotation
   #7 = Utf8               value
   #8 = Utf8               ()Ljava/lang/String;
#9 = Utf8               AnnotationDefault
  #10 = Utf8               default value
  #11 = Utf8               number
  #12 = Utf8               ()I
  #13 = Utf8               SourceFile
  #14 = Utf8               MyAnnotation.java
  #15 = Utf8               RuntimeVisibleAnnotations
  #16 = Utf8               Ljava/lang/annotation/Target;
  #17 = Utf8               Ljava/lang/annotation/ElementType;
  #18 = Utf8               TYPE
  #19 = Utf8               METHOD
  #20 = Utf8               Ljava/lang/annotation/Retention;
  #21 = Utf8               Ljava/lang/annotation/RetentionPolicy;
  #22 = Utf8               RUNTIME
{
  public abstract java.lang.String value();
    descriptor: ()Ljava/lang/String;
    flags: (0x0401) ACC_PUBLIC, ACC_ABSTRACT
    AnnotationDefault:
      default_value: s#10
        "default value"

  public abstract int number();
    descriptor: ()I
    flags: (0x0401) ACC_PUBLIC, ACC_ABSTRACT
}
SourceFile: "MyAnnotation.java"
RuntimeVisibleAnnotations:
  0: #16(#7=[e#17.#18,e#17.#19])
    java.lang.annotation.Target(
      value=[Ljava/lang/annotation/ElementType;.TYPE,Ljava/lang/annotation/ElementType;.METHOD]
    )
  1: #20(#7=e#21.#22)
    java.lang.annotation.Retention(
      value=Ljava/lang/annotation/RetentionPolicy;.RUNTIME
    )

```

## meta annotation



## compose annotation



## reflect annotation

## @Target

## @Retention