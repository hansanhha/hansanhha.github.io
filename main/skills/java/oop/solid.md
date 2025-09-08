---
layout: default
title:
---

[SOLID](#solid)

[Single Responsibility Principal, SRP](#single-responsibility-principal-srp)

[Open-Closed Principal, OCP](#open-closed-principal-ocp)

[Liskov Substitution Principal, LSP](#liskov-substitution-principal-lsp)

[Interface Segregation Principal, ISP](#interface-segregation-principal-isp)

[Dependency Inversion Principal, DIP](#dependency-inversion-principal-dip)

## SOLID

로버트 C. 마틴(Uncle Bob)가 설립한 객체지향 설계 원칙으로, 각 원칙의 맨 앞 글자를 따서 SOLID 원칙이라고 한다

객체지향이 가진 장점인 유연성(flexiable), 유지보수성(maintainable), 가독성(understandable)을 발휘할 수 있는 객체 설계 방법을 제안한다

## Single Responsibility Principal, SRP

**엔티티/모듈(클래스, 메서드, 라이브러리 등)은 단 하나의 책임을 가져야 한다**는 원칙 (또는 변경해야 할 이유가 한 개보다 많으면 안된다)

로버트 C. 마틴은 책임이란 "변경의 이유"라고 정의한다

같은 이유로 변경해야 할 것들은 함께 모으고, 다른 이유로 변경해야 할 것들은 분리해야 한다는 의미이다 (관심사 분리)

만약 특정 모듈이 여러 책임을 지니고 있으면 요구사항에 따라 여러 이유에 의해 변경될 것이고 의도치 않은 부작용이 발생할 가능성이 높아진다

**여러 책임을 가진 클래스**

```java
class Developer {
    
    public void work() {
        meeting();
        writingCode();
        makingCoffee();
    } 
}
```

**SRP를 준수한 객체 설계**

```java
class Developer {
    
    public void writingCode() {}
}

class Manager {
    
    public void meeting() {}
}

class Barista {
    
    public void makingCoffee() {}
}
```

## Open-Closed Principal, OCP

**엔티티는 확장에는 열려있어야 하고, 수정에는 닫혀야 한다**는 원칙

개발자는 기존 코드를 그대로 유지하면서 새로운 기능을 추가할 수 있어야 한다는 의미이다

OCP를 준수하지만 SRP를 준수하지 않는 경우 코드 변경 없이 확장할 수 있지만 클래스가 여러 책임을 떠안아 코드의 복잡도를 높인다

또한 모듈의 책임이 과도하게 집중되는 것은 변경이 다른 부분에 의도치 않게 영향을 미치는 부작용을 초래할 수 있다

```java
class Developer {
    
    private FrontendCode frontendCode;
    private BackendCode backendCode;
    private InfraCode infraCode;
    
    public void work(String part) {
        if (part.equals("frontend")) {
            developFrontend();
        }
        else if (part.equals("backend")) {
            developBackend();
        }
        else if (part.equals("infra")) {
            developInfra();
        }
    }
    
    private void developFrontend() {}
    private void developBackend() {}
    private void developInfra() {}
}
```

```java
Develper frontDeveloper = new Developer();
Developer backDeveloper = new Developer();

frontDeveloper.work("frontend");
backDeveloper.work("backend");
```

위의 코드는 새로운 개발 파트를 추가할 때 기존 메서드를 수정하지 않고, 조건문에 새로운 개발 메서드만 추가하면 된다 (OCP 준수)

하지만 특정 Developer 클래스가 프론트엔드, 백엔드, 인프라 등 모든 파트의 책임을 가지고 있기에 여러 가지 변경 이유가 존재하고, 각 파트가 로직과 강하게 결합되어 한 부분의 변경이 다른 부분에 영향을 미칠 수 있다 (SRP 위반)

**OCP와 SRP를 함께 준수한 코드**

```java
interface Developer {
    void work();
}

class FrontendDeveloper implements Developer {

    private FrontendCode frontendCode;

    public void work() {
        System.out.println("developing frontend code ...");
    }
}

class BackendDeveloper implements Developer {

    private BackendCode backendCode;

    public void work() {
        System.out.println("developing backend code ...");
    }
}

class InfraDeveloper implements Developer {

    private Map<String, Infrastructure> infrastructureMap;
    private InfraCode infraCode;

    public void work() {
        System.out.println("developing infra code ...");
    }
}
```

```java
Developer frontDeveloper = new FrontendDeveloper();
Developer backDeveloper = new BackendDeveloper();

frontDeveloper.work();
backDeveloper.work();
```

OCP와 SRP를 함께 준수하면 객체의 책임 분리를 통해 코드가 단순해지면서 확장에도 유연해진다

### OCP 준수 방법

#### OCP 준수 첫 번째 방법: 인터페이스와 다형성 활용, 전략 패턴

인터페이스나 추상 클래스를 통해 핵심 로직을 정의하고, 구체적인 구현은 이를 상속받아서 확장하는 방법이다

인터페이스 구현체를 생성하면 기존 코드를 수정하지 않고 새로운 기능을 추가할 수 있다

전략 패턴은 동적으로 객체의 행동(알고리즘)을 변경할 수 있는 디자인 패턴으로 객체의 다형성을 이용할 수 있는 방법 중 하나이다

**다형성 + 전략 패턴 코드**

```java
/* ------- PaymentStrategy 정의 ------- */

interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("using credit card");
    }
}

class BitcoinPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("using bitcoin");
    }
}

/* ------- PaymentStrategy 사용 -------- */

class PaymentService {
    
    private PaymentStrategy strategy;
    
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void processPayment(int amount) {
        strategy.pay(amount);
    }
}
```

#### OCP 준수 두 번째 방법: 컨테이너 기반 개발, IoC/DI

IoC 컨테이너를 사용하여 의존성을 주입하면 OCP를 준수하기 쉬워진다

**스프링의 DI 코드**

```java
public interface NotificationService {
    void notify(String message);
}

@Service
class EmailNotificationService implements NotificationService {
    public void notify(String message) {
        System.out.println("sending email");
    }
}

@Service
class SmsNotificationService implements NotificationService {
    public void notify(String message) {
        System.out.println("sending sms");
    }
}

@Contorller
class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationService(@Qualifier("smsNotificationService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

#### OCP 준수 세 번째 방법: 플러그인 아키텍처

시스템의 핵심 로직과 확장 기능을 분리하여 플러그인을 통해 동적으로 확장할 수 있는 아키텍처 설계 방법이다

#### OCP 준수 네 번째 방법: 람다식

자바 8부터 제공하는 함수형 인터페이스와 람다를 사용하여 동적으로 행동을 정의할 수 있다

```java
public class JavaDeveloper {
    
    public void doTask(Supplier<String> problem) {
        String unsolvedProblem = problem.get();
        coding(unsolvedProblem);
    }
}

JavaDeveloper developer = new JavaDeveloper();
developer.doTask(() -> "oop concept");
developer.doTask(() -> "solid principal understanding");
```

### Liskov Substitution Principal, LSP

**자식 클래스는 부모 클래스의 역할을 대체할 수 있어야 한다**는 원칙

```java
public class Cashier {

    private final Calculator calculator;
    
    public Cashier(Calculator calculator) {
        this.calculator = calculator;
    }

    public int calculate(int a, int b) {
        return calculator.add(a, b);
    }
}
```

위처럼 Cashier 클래스가 Calculator 클래스를 의존하고 있을 때, Calculator 클래스를 상속받은 OtherCalculator 클래스로 타입을 변경해도 메서드의 행동이나 결과가 변경되어서는 안된다

하위 구현체가 부모 클래스의 메서드를 오버라이딩하더라도 기존 메서드의 기대 결과값을 유지함으로써 클라이언트는 자유롭게 다른 구현체로 교체할 수 있다

### Interface Segregation Principal, ISP

**여러 개의 메서드가 정의되어 있는 큰 인터페이스를 여러 개의 작은 인터페이스로 나눠 분리**하는 원칙

클라이언트가 사용하지 않는 메서드에 의존하지 않도록 인터페이스를 분리함으로써 클라이언트가 필요한 메서드만 사용할 수 있게 한다

동시에 구현체가 불필요한 메서드를 구현하지 않고 특정 관심사에만 집중할 수 있게 한다

SRP와 마찬가지로 객체가 여러 책임을 가지고 있지 않게 하기 위한 원칙으로, 인터페이스가 특정 역할(행동)을 수행하도록 분리하는 것이 목적이다

**ISP 위반 코드**

```java
public interface GameDeveloper {
    
    void writeCode();
    void testCode();
    void deployArtifact();
    void play();
    void scheduleManagement();
}
```

위의 GameDeveloper 인터페이스는 여러 관심사의 메서드를 가지고 있어서 클라이언트는 사용하지 않는 메서드에 의존하게 될 뿐만 아니라 구현체는 불필요한 메서드를 구현해야 한다

**ISP 준수 코드**

```java
public interface GameDeveloper {
    
    void writeCode();
    void testCode();
    void deployArtifact();
}

public interface GamePlayer {
    
    void play();
}

public interface GameProjectManager {
    
    void scheduleManagement();
}
```

각 관심사에 따라 인터페이스를 분리함으로써 클라이언트는 필요한 메서드만 사용할 수 있고, 구현체는 불필요한 메서드를 구현하지 않아도 된다

### Dependency Inversion Principal, DIP

**의존성 주입을 통해 상위 모듈은 하위 모듈의 구현체에 의존하면 안되고, 둘 다 추상화에 의존해야 한다**는 원칙

상위 모듈에서 직접 하위 모듈의 구현체 생성을 수행하면 상위 모듈이 하위 모듈에 의존하게 된다

하위 모듈의 변경이 상위 모듈에 영향을 미치게 되며 상위 모듈이 하위 모듈에 강하게 결합되어 유연성이 떨어진다 (테스트 또한 어려워짐)

**DIP 위반 코드**

```java
class Restaurant {
    
    private final JapaneseChef japaneseChef;
    
    public Restaurant() {
        this.japaneseChef = new JapaneseChef();
    }
    
    public void serve() {
        japaneseChef.getSushi();
    }
}
```

위의 Restaurant 클래스는 생성자에서 직접 JapaneseChef 클래스를 생성하고 있어서 Restaurant 클래스가 JapaneseChef 클래스에 의존하게 된다

식당 운영 정책 변경으로 일식 요리가 아닌 다른 요리를 제공해야 할 경우 Restaurant 클래스를 수정해야 한다

**DIP 준수 코드**

```java
class Restaurant {
    
    private final Chef chef;
    
    public Restaurant(Chef chef) {
        this.chef = chef;
    }
    
    public void serve() {
        chef.cook();
    }
}
```

```java
interface Chef {
    Food cook();
}

class JapaneseChef implements Chef {
    
    public Food cook() {
        return new Sushi();
    }
}

class ItalianChef implements Chef {
    
    public Food cook() {
        return new Pasta();
    }
}
```

Restaurant 클래스는 특정 Chef 클래스가 아닌 인터페이스를 생성자로부터 주입받음으로써 다양한 Chef 구현체를 통해 여러 요리를 제공할 수 있는 유연성을 가진다

Chef 구현체가 변경되어도 Restaurant 클래스를 수정할 필요가 없고 테스트 코드 작성이 용이해진다

또한 구현체 역시 인터페이스에 의존하고 있기 때문에 기존 코드의 변경을 유발하지 않으면서 다른 Chef 구현체를 쉽게 추가할 수 있다
