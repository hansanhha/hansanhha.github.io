package hansanhha.counter;

import io.micrometer.core.annotation.Counted;

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
