---
layout: default
title:
---

#### index
- [Counter](#counter)
- [@Counted](#counted)
- [FunctionCounter](#functioncounter)


## Counter

counter는 단순한 카운트 값을 수집하는 메트릭으로, 마이크로미터에서 제공하는 Counter 인터페이스을 사용하면 고정된 양(양수)만큼 값을 증가시킬 수 있다

`Counter` 인터페이스는 주로 이벤트 발생 횟수를 추적하는 데 사용하며 increment 메서드로 증가만 할 수 있는 특징을 가지며 누적 통계를 필요로 할 때 사용한다

```java
public class OrderCounter {

    private final Counter orderCounter;

    /*
         shallowOrder.created.count 라는 이름으로 카운터 정의 (마이크로미터 네이밍 컨벤션: 소문자, '.'으로 단어 구별)
         type=online 태그 설정
     */
    public OrderCounter(MeterRegistry registry) {
        orderCounter = Counter.builder("shallowOrder.created.count")
                .description("total number of orders created")
                .tags("type", "online")
                .register(registry);
    }

    public void createOrder() {
        System.out.println("created shallowOrder");

        orderCounter.increment();
    }
}
```


## @Counted

`@Counted` 어노테이션은 aspectj aop 기반으로 메서드 호출 횟수를 자동으로 카운팅한다

참고로 프록시 패턴을 사용하기 때문에 프록시 객체 내부에서 자기 자신을 호출하면 카운팅되지 않는다

```java
public class PaymentCounter {

    /*
         aspectj aop를 기반으로 동작하는 @Counted 어노테이션 사용
         메서드 호출 횟수를 자동으로 카운팅하며, aop 방식으로 동작하기 때문에 프록시 객체 내부에서 호출하면 카운팅되지 않는다
     */
    @Counted(value = "payment.success.count", description = "number of successful payments")
    public void processPayment() {
        System.out.println("payment proceed successfully");
    }
}
```

### @MeterTag

@Counted나 @Timed 등과 함께 사용하는 어노테이션으로 메서드 호출 시점에 파라미터 값을 메트릭 태그로 활용한다

```java
public class PaymentCounter {
    /*
        @MeterTag 어노테이션은 어노테이션에 지정한 키와 파라미터의 값의 쌍이 메트릭 태그에 추가한다
    */
    @Counted(value = "payment.failure.count", description = "number of failure payments")
    public void failPayment(@MeterTag(key = "cause") String failureCause) {
        System.out.println("payment failed (cause: " + failureCause + ")");
    }
}
```


## FunctionCounter

`FunctionCounter`는 현재 상태를 기반으로 동적으로 값을 계산하는 인터페이스로 값을 누적하지 않고 현재 상태를 나타낸다

현재 메모리 사용량이나 작업 큐의 크기처럼 변동하는 값을 카운팅할 때 사용한다

```java
public class TaskQueue {

    private final AtomicInteger queueSize = new AtomicInteger(0);

    // FunctionCounter는 값을 누적하지 않고 현재 상태를 기반으로 동적으로 값을 계산한다
    public TaskQueue(MeterRegistry registry) {
        FunctionCounter.builder("task.queue.size", queueSize, AtomicInteger::get)
                .description("current size of the task queue")
                .register(registry);
    }

    public void addTask() {
        queueSize.incrementAndGet();
        System.out.println("task added to the queue");
    }

    public void completeTask() {
        queueSize.decrementAndGet();
        System.out.println("task completed");
    }

}
```