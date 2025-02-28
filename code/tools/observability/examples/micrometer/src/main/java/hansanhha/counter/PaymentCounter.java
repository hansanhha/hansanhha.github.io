package hansanhha.counter;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.aop.MeterTag;

public class PaymentCounter {

    /*
         aspectj aop를 기반으로 동작하는 @Counted 어노테이션 사용
         메서드 호출 횟수를 자동으로 카운팅하며, aop 방식으로 동작하기 때문에 프록시 객체 내부에서 호출하면 카운팅되지 않는다
     */
    @Counted(value = "counter.payment.success.count", description = "number of successful payments")
    public void processPayment() {
    }

    /*
        @MeterTag 어노테이션은 어노테이션에 지정한 키와 파라미터의 값의 쌍이 메트릭 태그에 추가한다
     */
    @Counted(value = "counter.payment.failure.count", description = "number of failure payments")
    public void failPayment(@MeterTag(key = "cause") String failureCause) {
    }

}
