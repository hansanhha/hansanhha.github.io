package hansanhha;

import hansanhha.counter.OrderCounter;
import hansanhha.counter.PaymentCounter;

import hansanhha.counter.TaskQueue;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class MicrometerApplication {

    public static void main(String[] args) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        useCounter(registry);
    }

    private static void useCounter(MeterRegistry registry) {
        // Counter 인터페이스 사용
        OrderCounter orderCounter = new OrderCounter(registry);
        orderCounter.createOrder();

        // @Counted 어노테이션 사용
        PaymentCounter paymentCounter = new PaymentCounter();
        paymentCounter.processPayment();

        // FunctionCounter 인터페이스 사용
        TaskQueue taskQueue = new TaskQueue(registry);
        taskQueue.addTask();
        taskQueue.addTask();
        taskQueue.addTask();
        taskQueue.addTask();
        taskQueue.completeTask();
        taskQueue.completeTask();

        registry.forEachMeter(meter -> {
            if (meter.getId().getType().equals(Meter.Type.COUNTER)) {
                System.out.println("================================");
                System.out.println("name: " + meter.getId().getName());
                System.out.println("tag:" + meter.getId().getTags());
                System.out.println("measurements:" + meter.measure());
            }
        });
    }
}
