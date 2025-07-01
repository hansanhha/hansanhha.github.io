package hansanhha.counter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class OrderCounter {

    private final Counter orderCounter;

    /*
         Counter 인터페이스를 사용하여 프로그래밍 방식으로 카운팅
         order.created.count 라는 이름으로 카운터 정의 (마이크로미터 네이밍 컨벤션: 소문자, '.'으로 단어 구별)
         type=online 태그 설정
     */
    public OrderCounter(MeterRegistry registry) {
        orderCounter = Counter.builder("counter.order.count")
                .description("total number of orders created")
                .tags("type", "online")
                .register(registry);
    }

    public void createOrder() {
        orderCounter.increment();
    }
}
