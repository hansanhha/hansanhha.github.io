[class definition](#class-definition)

[constructor](#constructor)

[new keyword, class instantiation/initiation](#new-keyword-class-instantiationinitiation)

[this keyword, self-reference](#this-keyword-self-reference)

[access modifier, package-private](#access-modifier-package-private)

[static keyword](#static-keyword)

[final keyword](#final-keyword)


## class definition

자바에서 클래스는 객체를 생성하기 위한 blueprint 역할을 하며 필드(객체의 데이터)와 메서드(객체의 행동)로 구성된다

#### 필드

클래스 내부에 선언된 변수로 객체의 상태(state)를 저장한다

인스턴스화 초기화 과정 시 필드를 초기화하지 않으면 해당 필드는 각 자료형에 맞는 기본 값이 할당된다

#### 메서드

클래스의 동작(behavior)을 정의하는 함수이다

객체의 상태를 변경하거나 특정 로직을 구성하여 값을 반환할 수 있다

```java
public class BasicClass {

    String name;
    int value;

    public String print() {
        return "name: " + name + "value: " + value; 
    }

}
```

### 자바 파일의 클래스 정의 규칙

하나의 .java 파일에는 하나의 public 클래스만 정의할 수 있다

또한 public 클래스는 다른 패키지에서도 접근할 수 있어야 하기 때문에 정확한 위치를 보장하기 위해 컴파일러는 public class를 포함하는 .java 파일의 이름과 public class의 이름이 동일하도록 강제한다

다음과 같이 public class 대신 class로 선언하면 자바 파일에 여러 개의 클래스를 정의할 수 있다

```java
public class BasicClass {
}

class AnotherClass {
}
```

이 경우 컴파일 시 각각 .class 파일로 생성되며, public이 아닌 class는 같은 패키지에서만 접근할 수 있다


## constructor

생성자는 객체 생성 시 호출되는 특수한 메서드로 필드를 초기화하는 역할을 수행한다

생성자를 통해서 클래스를 인스턴스화할 수 있다

생성자 메서드 이름은 클래스 이름과 동일하며 반환 타입을 명시하지 않는다

또한 아래와 같이 this()를 사용하여 클래스에 정의된 다른 생성자를 호출할 수 있다

```java
public class BasicClass {

    String name;
    int value;

    public BasicClass(String name) {
        this(name, 1);
    }

    public BasicClass(int value) {
        this("test", value);
    }

    public BasicClass(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
```

### 기본 생성자

생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 생성한다

### 생성자 접근 제어

클래스의 인스턴스화는 오로지 생성자를 통해서만 수행된다

생성자의 접근 제어자를 이용하여 외부에서의 생성자 접근을 조절할 수 있다

아래와 같이 기본 생성자를 private으로 제한해 외부에서 아예 인스턴스를 만들 수 없게 할 수도 있다 

```java
public class PrivateConstructorClass {

    private PrivateConstructorClass() {
    }
}
```


## new keyword, class instantiation/initiation

아래와 같이 new 키워드와 생성자를 통해 클래스를 인스턴스화할 수 있다

new 키워드는 인스턴스를 heap 메모리에 할당하고 생성자를 호출하여 초기화한 후 객체의 참조(reference)를 반환한다

```java
BasicClass b = new BasicClass("test", 1);
```

### 객체 생성 과정

클래스 로딩: jvm classloader에 의해 jvm 메모리에 클래스가 로딩된다

메모리 할당: heap 영역에 해당 인스턴스의 메모리 공간을 확보한다

필드 초기화: 필드의 초기화 과정을 수행한다

참조 반환: 객체의 주소를 참조변수에 반환한다

### 필드 초기화 순서

인스턴스가 생성될 때 초기화 순서는 다음과 같다 

#### 1. 각 필드의 자료형에 맞는 기본 값 할당

숫자: 0

boolean: false

참조(String 등): null

#### 2. 필드 명시적 초기화

필드 선언과 동시에 초기화하면 기본값 대신 명시적인 값이 할당된다

```java
String name = "hello";
int value = 10;
```

#### 3. 인스턴스 초기화 블록 호출

클래스 내에서 {}로 정의된 블록을 인스턴스 초기화 블록이라고 한다

생성자가 실행되기 전에 초기화 블록이 실행되며 여러 개가 있는 경우 작성된 순서대로 실행된다

```java
{
    name = "initiation block";
    value = 100;
}

{
    name = "initiation block 2";
    value = 200;
}
```

#### 4. 생성자 호출

최종적으로 생성자가 실행되며 초기화가 완료된다 

생성자 내부에서 필드를 다시 초기화할 수 있다

#### 기타 - static 초기화 블록

클래스가 처음 jvm 메모리에 로드될 때 단 한번만 호출되며 이후 재실행되지 않는 초기화 블록이다

static 필드를 초기화할 수 있다


## this keyword, self-reference

this 키워드는 현재 인스턴스를 참조하는 키워드로 자기 자신의 참조가 필요한 경우 this 키워드를 사용한다

주로 **필드와 지역 변수의 충돌 방지**나 **다른 생성자 호출**을 위해 사용한다

```java
// 필드와 지역 변수를 구분한다
// 현재 객체의 메서드를 호출한다
public void setName(String name) {
    this.name = name;
    System.out.println(this.print());
}
```


## access modifier, package-private

| 접근 제어자    | 같은 클래스 | 같은 패키지 | 자식 클래스 | 외부 클래스 |
|-----------|--------|--------|--------|--------|
| public    | O      | O      | O      | O      |
| protected | O      | O      | O      | X      |
| default   | O      | O      | X      | X      |
| private   | O      | X      | X      | X      |

package private: 접근 제한자를 명시하지 않으면 같은 패키지에서만 접근할 수 있다


## static keyword

static 키워드는 클래스 레벨에서 공유되는 멤버를 정의할 때 사용한다

#### static 변수

클래스의 모든 인스턴스가 공유하는 변수로 `클래스명.변수명`으로 접근할 수 있다

#### static 메서드

인스턴스 생성 없이 호출 가능한 메서드로 `클래스명.변수명`으로 접근할 수 있다

static 메서드는 static 변수만 사용할 수 있으며 인스턴스 변수에는 접근할 수 없다

#### static 블록

클래스가 jvm 메모리에 처음 로드될 때 한 번만 실행되는 초기화 블록으로 주로 정적 변수를 초기화할 때 사용한다


## final keyword

클래스에서 final 키워드를 사용하면 오버라이딩을 제한한다

#### final 변수

변경할 수 없는 필드(상수값)를 정의할 때 사용한다

#### final 메서드

final 메서드는 자식 클래스가 오버라이딩할 수 없게 제한한다

#### final 클래스

final 클래스는 더 이상 상속할 수 없게 제한한다


