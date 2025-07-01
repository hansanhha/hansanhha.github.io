---
layout: default
title:
---

[GRASP](#grasp)

[Information Expert](#information-expert)

[Creator](#creator)

[Controller](#controller)

[Indirection](#indirection)

[Loose Coupling](#loose-coupling)

[High Cohesion](#high-cohesion)

[Polymorphism](#polymorphism)

[Protected Variations](#protected-variations)

[Pure Fabrication](#pure-fabrication)

## GRASP

GRASP(General Responsibility Assignment Software Patterns(Principles))는 소프트웨어 설계 원칙으로 소프트웨어 개발 프로젝트에서 자주 맞닥뜨리는 객체 설계와 책임 할당에 대한 문제를 해결할 수 있는 9가지 패턴을 제안한다

Information Expert, Creator, Low Coupling, High Cohesion, Controller, Polymorphism, Pure Fabrication, Indirection, Protected Variations

## Information Expert

객체에 책임을 할당해야 하는 기본 원칙은 **정보를 알고 있어야 하거나, 책임을 수행하는 데 필요한 데이터를 잘 알고 있는 객체에게 책임을 할당**하는 것으로 메서드나 필드 등으로 책임을 위임할 위치를 결정하기 위해 사용되는 원칙이다

이는 객체의 데이터와 행동을 일치시켜 응집도를 높이는데 기여한다

**은행 계좌와 잔액 관리 예시 코드**

```java
class Account {
    
    private double balance;
    
    public Account(double balance) {
        this.balance = balance;
    }
    
    public boolean withdraw(double amount) {
        if (balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

```java
class ATM {
    
    public void processWithdraw(Account account, double amount) {
        if (account.withdraw(amount)) {
            System.out.println("Withdrawal successful. Remaining balance: " + account.getBalance());
        } else {
            System.out.println("Insufficient balance");
        }
    }
}
```

Account는 잔액 정보(balance)를 가지고 있는 객체로서 잔액 관리 책임을 수행하기 충분하므로 withdraw, deposit, getBalance 메서드를 가지고 있다

ATM은 특정 계좌에 대한 잔액 데이터를 가지고 있을 수 없기 때문에 잔액 관리 책임을 수행하기 적절치 않다

따라서 계좌 객체에게 출금 처리를 위임하고, 출금 처리 결과를 출력하는 책임을 가지고 있다

Account 객체의 내부 구현이 변경되더라도 ATM 객체는 영향을 받지 않는다

Information Expert 원칙을 적용함으로써 책임이 적절히 분배돼서 응집도가 높아지고 결합도는 낮출 수 있다

## Creator

**객체 생성 책임을 어디에 할당할지 결정하는 데 도움을 주는 원칙**으로 객체 간 의존성을 관리하기 위해 사용된다

Creator 원칙은 다음 기준에 부합되는 경우 A 객체가 B 객체를 생성하도록 권장한다

- A 객체가 B 객체를 포함하거나 Aggregate 관계를 가지는 경우
- A 객체가 B 객체를 사용하는 경우
- A 객체가 B 객체의 데이터를 보유하고 있는 경우 (초기화에 필요한 데이터를 A 객체가 알고 있는 경우)
- A 객체가 B 객체의 생애 주기를 관리해야 하는 경우 (B 객체의 생성과 소멸을 A 객체가 제어하는 경우)

**Creator 예시 코드**

```java
class Order {
    
    private List<Item> orderItems = new ArrayList<>();
    
    public void addItem(String name, int price) {
        Item item = new Item(name, price);
        orderItems.add(item);
    }
}
```

```java
class Item {
    
    private String name;
    private int price;
    
    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    // getter, setter
}
```

위의 Order 클래스는 Item 객체를 포함(Aggregation)하고 있는 구조이므로, Item 객체 생성의 책임을 Order에게 할당한다

Creator 원칙은 객체 생성 로직을 중앙화하여 명확한 책임 분배를 할 수 있지만 복잡한 객체 관계에서는 객체 생성 책임을 할당하기 애매한 경우가 있다

이를 완화하기 위해 팩토리 패턴 또는 DI를 활용할 수 있다

## Controller

**사용자의 요청이나 시스템 이벤트를 처리하는 책임을 담당할 객체를 지정**하는 데 도움을 주는 설계 원칙으로, 애플리케이션의 흐름 제어와 UI 요소(화면, 버튼, 이벤트 핸들러 등) 간의 결합을 줄이기 위해 사용된다

컨트롤러는 UI 계층 이후 맨 앞에 위치하여 **특정 유스 케이스(하나 이상)**에 대한 **요청(시스템 이벤트)를 수신**하고 다른 객체에게 **처리를 위임**하는 역할을 가진 객체이다 (사용자 입력과 시스템 로직 사이의 매개 역할을 수행하는 객체)

일반적으로 "Handler"나 "Controller" 이름을 붙인다

컨트롤러 원칙의 목적
- UI와 비즈니스 로직 분리
- 재사용성 향상: 동일한 컨트롤러가 다양한 UI나 클라이언트(웹/모바일)를 처리할 수 있음
- 변경 용이성: UI나 비즈니스 로직이 변경될 경우 영향을 최소화함

**웹 애플리케이션 컨트롤러**

```java
@RestController
@RequestMapping("/uesrs")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created");
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }
}
```

**게임 애플리케이션 컨트롤러**

```java
class GameController {
    private Player player;
    private GameWorld gameWorld;

    public GameController(Player player, GameWorld gameWorld) {
        this.player = player;
        this.gameWorld = gameWorld;
    }

    public void movePlayer(String direction) {
        switch (direction) {
            case "UP" -> player.moveUp();
            case "DOWN" -> player.moveDown();
            case "LEFT" -> player.moveLeft();
            case "RIGHT" -> player.moveRight();
        }
    }

    public void attack() {
        gameWorld.handleAttack(player);
    }
}
```

## Indirection

**두 요소(또는 그 이상) 간 결합도를 낮추고 재사용성을 높이기 위해 중재 역할을 하는 객체를 도입**하는 원칙이다

두 객체 간의 직접 통신을 피하고 중재자(Mediator) 객체에게 요청을 전달하거나 필요한 로직을 처리한다

대표적으로 MVC 패턴에서 데이터(model)과 화면(view) 사이에서 흐름을 제어하는 컨트롤러 객체가 중재 역할을 수행한다

**메시지 브로커를 활용한 간접 통신**

```java
class MessageBroker {

    private final List<Subsriber> subscribers = new ArrayList<>();
    
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
    
    public void publish(String message) {
        subscribers.stream().forEach(subscriber -> subscriber.receive(message));
    }
}
```

```java
public interface Subscriber {
    void receive(String message);
}

public class EmailSubscriber implements Subscriber {
    
    @Override
    public void receive(String message) {
        System.out.println("Email received: " + message);
    }
}

public class SmsSubscriber implements Subscriber {
    
    @Override
    public void receive(String message) {
        System.out.println("SMS received: " + message);
    }
}
```

MessageBroker 클래스는 중재자로 동작하여 EmailSubscriber와 SmsSubscriber 간의 직접적인 통신을 제거한다

새로운 Subscriber를 추가해도 기존 코드의 변경이나 다른 클래스에 영향을 주지 않는다

**Indirection 활용**
- MVC 아키텍처: 컨트롤러가 뷰와 모델 간의 중재자 역할을 수행함
- 이벤트 기반 시스템(EDA): 이벤트 브로커를 통해 이벤트 전달
- 의존성 주입: 특정 객체 생명주기 관리를 중재자가 담당

## Loose Coupling

결합(커플링)이란 어떤 한 요소가 다른 요소에 얼마나 강하게 연결되어 있는지, 알고 있는지, 의존하고 있는지를 말한다

느슨한 결합이란 **본래 객체가 수행해야 할 책임을 수행하면서 결합도를 낮추기 위한 방식을 갖추는 것**을 말한다
- 클래스간 낮은 의존성
- 변경이 발생한 경우 다른 객체에 영향을 최소화
- 높은 재사용성

반대로 강한 결합(Tight Coupling)은 모듈이 서로 구체적인 구현에 의존하여, 한 쪽을 변경하면 다른 쪽도 변경해야 한다

**느슨한 결합 구현 방법**
- 구체 클래스에 의존하지 않고 인터페이스나 추상 클래스에 의존
- 의존성 주입
- 이벤트 기반 통신
- 중재자 패턴

## High Cohesion

높은 응집도는 객체의 코드 가독성, 유지보수성, 재사용성을 향상 시키기 위한 원칙으로 **모듈이 단일 책임이나 밀접하게 관련된 작업만을 수행하도록 설계**되는 것을 의미한다

반면 클래스나 모듈이 서로 다른 책임을 혼합하여 수행하면 낮은 응집도를 가져 코드의 명확성, 유지보수성을 흐리게 한다

**낮은 응집도 코드**

```java
class Utility {
    
    // 이메일 전송
    public void sendEmail(String recipient, String message) {
        
    }
    
    // 할인 계산
    public int calculateDiscount(double price) {
        
    }
    
    // URI 생성
    public URI createURI(String uri) {
        
    }
}
```

클래스가 이메일 전송, 할인 계산, URI 생성이라는 서로 관련 없는 작업을 모두 수행하고 있다

응집도가 낮은 코드는 변경이 발생하면 다른 객체나 로직에 영향을 줄 가능성이 크다

**높은 응집도 코드**

```java
public class EmailService {

    public void sendEmail(String recipient, String message) {

    }
}

public class DiscountCalculator {

    public int calculateDiscount(double price) {

    }
}

public class UriUtils {

    public URI createURI(String uri) {

    }
}
```

단일 책임(SRP)에 집중하도록 행위에 따라 클래스를 분리하여 응집도를 높인다 -> 유지보수, 테스트에 용이해짐

**높은 응집도와 낮은 결합도(High Cohesion + Loose Coupling)**
- High Cohesion은 **클래스 내부의 요소 간 관계**를 의미
- Loose Coupling은 **클래스 간의 관계**를 의미
- 두 원칙을 함께 사용하면 **클래스 내부 요소는 밀접하게 협력(High Cohesion)**하고, **클래스 간에는 의존성을 최소화(Loose Coupling)**할 수 있다

## Polymorphism

객체지향 특징 중 하나인 다형성은 **동일한 메시지(메서드 호출)에 대해 객체의 타입에 따라 다른 동작을 실행**할 수 있도록 설계하는 원칙이다

서로 다른 객체들이 동일한 인터페이스/추상 클래스를 구현하면 클라이언트는 인터페이스를 통해 객체와 상호작용하며, 구체적인 타입에 대해 알 필요가 없다

메서드 호출은 실행 시점에 객체의 실제 타입에 따라 호출된다 (동적 바인딩)

다형성을 활용하면 새로운 타입을 추가하더라도 기존 코드를 수정하지 않고 확장할 수 있다 (OCP 준수)

**다형성 대신 조건문을 사용한 코드**

```java
public class PaymentService {
    
    public void processPayment(String paymentType) {
        if (paymentType.equals("CreditCard")) {
            
        }
        else if (paymentType.equals("Bitcoin")) {
            
        }
        else {
            
        }
    }
}
```

분기 처리를 위해 조건문을 사용하면 코드 가독성이 떨어지고, 새로운 결제 수단이 추가될 때마다 조건문도 추가해야 한다

**다형성을 활용한 코드**

```java
/* ------- PaymentStrategy 정의 ------- */

interface PaymentMethod  {
    void pay(int amount);
}

class CreditCardPayment implements PaymentMethod  {
    public void pay(int amount) {
        System.out.println("using credit card");
    }
}

class BitcoinPayment implements PaymentMethod  {
    public void pay(int amount) {
        System.out.println("using bitcoin");
    }
}

/* ------- PaymentStrategy 사용 -------- */

class PaymentService {

    private PaymentMethod method;

    public void setStrategy(PaymentMethod  method) {
        this.method = method;
    }

    public void processPayment(int amount) {
        method.pay(amount);
    }
}
```

PaymentService 클래스는 PaymentMethod 인터페이스를 참조하므로 불필요한 분기 처리를 하지 않으며 앞으로 새로운 결제 방식이 추가되더라도 기존 코드를 수정할 필요 없다

**다형성 활용 패턴**
- 전략 패턴: 알고리즘을 인터페이스로 저으이하고, 실행 시점에 선택적으로 사용
- 팩토리 패턴: 다형성을 통해 객체 생성 처리
- 템플릿 메서드: 공통 로직은 상위 클래스에 정의하고, 세부 구현은 하위 클래스에서 제공

## Protected Variations

**변경 가능성이 높은 요소(변화점)를 식별하고, 이 변화로부터 다른 부분을 보호하기 위해 추상화와 캡슐화를 사용**하는 원칙이다

변경의 영향을 최소화하고 시스템 안정성과 유연성을 유지하기 위해 사용된다

protected variations
- 변화 예측: 시스템 내에서 자주 변경될 가능성이 있는 요소 식별(db, ui, 외부 api 등)
- 추상화: 인터페이스, 추상 클래스, 디자인 패턴을 사용해 변경 가능성이 높은 요소의 구체적인 구현을 숨긴다
- 변화로부터 보호: 변경 가능성이 높은 요소와 클라이언트 간의 의존성을 제거하여 변경이 발생하더라도 영향을 최소화

**예시 코드**

결제 시스템에서 결제 방식(CredicardPayment, BitcoinPayment 등)이 자주 변경될 수 있다고 가정

```java
interface PaymentMethod  {
    void pay(int amount);
}

class CreditCardPayment implements PaymentMethod  {
    public void pay(int amount) {
        System.out.println("using credit card");
    }
}

class BitcoinPayment implements PaymentMethod  {
    public void pay(int amount) {
        System.out.println("using bitcoin");
    }
}
```

```java
public class PaymentProcessor {
    public void processPayment(PaymentMethod paymentMethod) {
        paymentMethod.processPayment();
    }
}
```

공통 인터페이스를 정의하여 새로운 결제 방식이 추가되더라도 기존 코드를 수정하지 않게 하고, 클라이언트가 구체적인 결제 방식에 의존하지 않게 함

## Pure Fabrication

도메인 객체가 지나치게 많은 책임을 가지는 것을 방지하기 위한 원칙으로 **설계 과정에서 특정 책임을 도메인 개념과는 독립적으로 만들어진 별도의 객체에서 처리**한다 

도메인 객체가 본질적인 책임에만 집중하고 시스템의 인프라 코드 간의 결합도를 줄이기 위함

도메인 모델 책임 제한
- 도메인 객체 자체에 추가하는 것이 부자연스럽거나 불가능한 책임을 분리함
- 파일 저장, 데이터베이스 작업, 로그 기록 등

독립적인 클래스 생성
- 특정 책임을 수행하기 위한 도메인 모델과 무관한 클래스 설계
- 응집도가 높고 재사용성이 뛰어나야 함

**도메인 객체가 부가적인 책임을 가진 경우**

```java
class Order {

    private String orderId;
    private List<String> orderItems;

    // 주문 로직
    public void shallowOrder() {
        
    }
    
    // 파일 저장 로직 (I/O 작업)
    public void saveToFile() {
        
    }
}
```

도메인 객체 Order가 도메인 로직 외에 주문 데이터를 파일에 저장하는 책임을 가지고 있음

응집도와 재사용성이 떨어지고, 파일 저장 로직 변경 시 Order 클래스 자체를 수정해야 함

**pure fabrication 적용**

```java
public class OrderFileManager {

    // 파일 저장 로직
    public void saveToFile(Order shallowOrder) {
    }
}
```

```java
public class Order {
    private String orderId;
    private List<String> orderItems;

    // 주문 로직
    public void shallowOrder() {
        
    }

}
```

별도의 클래스를 만들어 도메인 로직과 파일 저장 로직을 분리함으로써 Order 클래스는 도메인 로직에만 집중할 수 있음

파일 저장 로직을 다른 도메인 클래스에서 사용할 수 있고 추후에 변경하더라도 OrderFileManager 클래스만 수정하면 된다

관련 디자인 패턴
- dao: 데이터베이스 작업을 캡슐화하여 도메인 객체와 분리
- 서비스 클래스: 비즈니스 로직을 도메인 객체와 분리
