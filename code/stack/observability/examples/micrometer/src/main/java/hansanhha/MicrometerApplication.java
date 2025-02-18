package hansanhha;

import hansanhha.distribution_summary.ResponseSizeTracker;
import hansanhha.distribution_summary.ScalingBucketCardinality;
import hansanhha.gauge.SpecialGauge;
import hansanhha.timer.BasicTimer;
import hansanhha.timer.FunctionTimers;
import hansanhha.timer.SampleTimer;
import hansanhha.timer.TimedAOP;
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

//        counter(registry);
//        gauge(registry);
//        timer(registry);
        distributionSummary(registry);
    }

    private static void counter(MeterRegistry registry) {
        printSeparator("counter");

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
        printSeparator("gauge");

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

    private static void timer(MeterRegistry registry) {
        printSeparator("timer");

        BasicTimer basicTimer = new BasicTimer(registry);
        basicTimer.executeRunnableTask();
        basicTimer.executeSupplierTask();

        SampleTimer sampleTimer = new SampleTimer(registry);
        sampleTimer.startTimer();
        sampleTimer.doSomething();
        sampleTimer.stopTimer();

        TimedAOP timedAOP = new TimedAOP();
        timedAOP.executeTask();
        timedAOP.executeLongTask();
        timedAOP.executeTask("task");

        FunctionTimers functionTimer = new FunctionTimers(registry);
        functionTimer.executeTask();
        functionTimer.executeTask();
        functionTimer.executeOther();
        functionTimer.executeOther();

        printMeter(registry, Meter.Type.TIMER);
    }

    private static void distributionSummary(MeterRegistry registry) {
        printSeparator("distribution summary");

        ResponseSizeTracker responseSizeTracker = new ResponseSizeTracker(registry);
        responseSizeTracker.recordResponseSize(100);
        responseSizeTracker.recordResponseSize(200);
        responseSizeTracker.recordResponseSize(300);
        responseSizeTracker.recordResponseSize(400);

        ScalingBucketCardinality scalingBucketCardinality = new ScalingBucketCardinality(registry);
        scalingBucketCardinality.recordWeight(50);
        scalingBucketCardinality.recordWeight(20);
        scalingBucketCardinality.recordWeight(80);

        printMeter(registry, Meter.Type.DISTRIBUTION_SUMMARY);
    }

    private static void printMeter(MeterRegistry registry, Meter.Type type) {
        registry.forEachMeter(meter -> {
            if (meter.getId().getType().equals(type)) {
                System.out.println("-----------------------------------");
                System.out.println("- name: " + meter.getId().getName());
                System.out.println("- tag:" + meter.getId().getTags());
                System.out.println("- measurements:" + meter.measure());
                System.out.println("-----------------------------------");
            }
        });
    }

    private static void printSeparator(String name) {
        System.out.println();
        System.out.printf("============= %s =============\n", name);
        System.out.println();
    }
}
