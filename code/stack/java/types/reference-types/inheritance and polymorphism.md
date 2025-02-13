[inheritance, implementation](#inheritance-implementation)

[super keyword](#super-keyword)

[field hiding, method overriding](#field-hiding-method-overriding)

[memory management in inheritance, implementation](#memory-management-in-inheritance-implementation)

[ClassCastException](#classcastexception)

[instanceof, Class.isAssignableFrom()](#instanceof-classisassignablefrom)


## inheritance, implementation

클래스를 상속하는 경우 extends 키워드를 사용하고, 인터페이스를 구현하는 경우 implements 키워드를 사용한다

클래스가 다른 클래스를 상속하면 두 클래스는 부모 - 자식 관계가 되고 부모 클래스의 부모 클래스, 조상 클래스까지 간접적으로 상속 관계를 가지게 되며 최종적으로 Object 클래스를 상속한다 

상속 관계에 놓인 부모, 조상 클래스의 private 접근 제어자로 선언된 필드와 메서드를 제외한 나머지 필드와 메서드를 상속받는다 (부모 클래스가 다른 패키지에 위치한 경우 default 접근 제어자 필드와 메서드는 상속 대상에 제외된다)


## super keyword

this 키워드가 현재 인스턴스에 대한 자기 참조 키워드라면 super 키워드는 현재 인스턴스의 부모 참조 키워드이다

따라서 부모 생성자, 부모의 필드/메서드에 super 키워드를 사용하여 접근할 수 있다

또한 자식 클래스의 생성자는 반드시 **맨 처음**에 부모 클래스의 생성자를 호출해야 한다


## field hiding, method overriding

부모 클래스로부터 상속받은 필드와 메서드는 자식 클래스를 같은 이름으로 다시 정의할 수 있다

#### 필드를 부모 클래스의 필드와 같은 이름으로 정의하는 경우 

자바에서는 필드는 정적 바인딩(compile-time)이 적용되기 때문에 자식 클래스의 필드가 부모 클래스의 필드를 감춘다 (hiding)

따라서 타입 캐스팅을 통해 숨어있는 부모 변수에 접근할 수 있다

```java
Car car = new Car("test car", 100, 1);

System.out.println(car.name); // car 출력
System.out.println(((Vehicle)car).name); // vehicle 출력 
```

#### 메서드를 재정의하는 경우

부모 클래스의 메서드를 재정의(@Override)하는 경우(동일한 시그니처를 가진 메서드를 자식 클래스에서 정의)엔 동적 바인딩(run-time)이 적용되어 참조 타입이 아닌 실제 객체 타입의 메서드가 호출된다

동적 바인딩은 오버라이딩된 메서드는 런타임 시 jvm이 실제 객체 타입을 기준으로 실행할 메서드를 결정하는 방식이다


## memory management in inheritance, implementation

자바에서 클래스를 상속하면 부모 클래스의 멤버도 자식 객체 내부에 포함되며 생성된 객체의 메모리 구조는 다음과 같다

```text
     [Stack]                          [Heap]
┌───────────────────┐            ┌────────────────────┐
│ c (자식 타입 참조) │ ────▶ │ 자식 객체               │
└───────────────────┘            │ ┌────────────────┐ │
                               │ │ a = 10   │ │  ← Parent 필드
                               │ │ b = 20   │ │  ← Child 필드
                               │ └────────────────┘ │
                               └────────────────────┘
```

heap 영역
- 자식 객체가 생성될 때 부모 클래스의 필드와 메서드도 포함되어 객체 내부에 할당된다 (메서드의 경우 method area에 위치하며 런타임에 실제 타입을 기준으로 실행할 메서드를 결정한다)
- 즉 부모의 필드(a)와 자식 필드(b)가 함께 자식 객체에 존재한다 

stack
- 자식 타입의 참조변수가 자식 타입의 인스턴스 heap 메모리를 가리킨다


## ClassCastException

### upcasting

부모 타입으로 자식 타입을 참조하는 방식은 안전하다

```java
Parent p = new Child();
```

### downcasting

부모 타입 변수를 자식 타입으로 변환하려면 실제 인스턴스가 해당 자식 타입인지 확인해야 한다

실제 객체가 자식 타입이 아닌 경우 ClassCastException 예외가 발생한다

```java
Parent p = new Child();
Child c = (Child) p; // ok 실제 객체가 Child 타입이므로 downcasting 가능

Parent p = new Parent();
Child c = (Child) p; // ClassCastException 발생. 실제 타입이 Parent이므로 자식 객체로 downcasting이 불가능하다 
```

아래와 같이 instanceof 연산자를 사용하여 타입을 확인한 후 타입캐스팅을 하는 것이 안전하다 

```java
if (p instanceof Child) {
    Child c = (Child) p;
    System.out.println("다운캐스팅 성공");
} else {
    System.out.println("다운캐스팅 불가");
}
```


## instanceof, Class.isAssignableFrom()

instanceof 또는 Class.isAssignableFrom()을 통해 객체 타입을 확인할 수 있다  

### instanceof

런타임에 객체의 실제 타입을 확인하는 연산자

객체가 특정 클래스 또는 상위 클래스(인터페이스 포함)의 인스턴스인지 확인할 때 사용한다

컴파일 오류를 발생시켜 런타임 시 다운캐스팅 예외 발생을 방지할 수 있다 

```java
Parent p = new Child();

System.out.println(p instanceof Child);  // true
System.out.println(p instanceof Parent); // true
System.out.println(p instanceof String); // 컴파일 오류
```

### Class.isAssignableFrom()

런타임에 클래스 타입 간의 상속 관계를 체크하는 정적 메서드

객체가 아닌 클래스 타입(class 객체)끼리 비교할 때 사용한다

instanceof 연산자와 달리 컴파일 오류를 발생시키지 않고 런타임 시 다운캐스팅 예외가 발생할 수 있다

A.class.isAssignableFrom(B.class)는 “B가 A의 서브타입인가?“를 의미한다

```java
System.out.println(Child.class.isAssignableFrom(Parent.class)); // false
System.out.println(Parent.class.isAssignableFrom(Child.class)); // true
```









