package hansanhha;

import hansanhha.gauge.SpecialGauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import hansanhha.counter.OrderCounter;
import hansanhha.counter.PaymentCounter;
import hansanhha.counter.TaskQueue;
import hansanhha.gauge.BasicGauge;

public class MicrometerApplication {

    public static void main(String[] args) {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        System.out.println("============= counter =============");
        System.out.println();

        counter(registry);

        System.out.println();
        System.out.println("============= counter =============");
        System.out.println();

        System.out.println("============= gauge =============");
        System.out.println();

        gauge(registry);

        System.out.println();
        System.out.println("============= gauge =============");
        System.out.println();
    }

    private static void counter(MeterRegistry registry) {
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

        printMeter(registry, Meter.Type.COUNTER);
    }

    private static void gauge(MeterRegistry registry) {
        // Gauge 생성
        BasicGauge basicGauge = new BasicGauge(registry);
        basicGauge.numberGauge();
        basicGauge.listGauge();
        basicGauge.mapGauge();
        basicGauge.gaugeBuilder();

        // TimeGauge, MultiGauge 생성
        SpecialGauge specialGauge = new SpecialGauge(registry);
        specialGauge.timeGauge();
        specialGauge.multiGauge();

        printMeter(registry, Meter.Type.GAUGE);
    }

    private static void printMeter(MeterRegistry registry, Meter.Type type) {
        registry.forEachMeter(meter -> {
            if (meter.getId().getType().equals(type)) {
                System.out.println("-----------------------------------");
                System.out.println("name: " + meter.getId().getName());
                System.out.println("tag:" + meter.getId().getTags());
                System.out.println("measurements:" + meter.measure());
            }
        });
    }
}
