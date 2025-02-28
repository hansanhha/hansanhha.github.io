package hansanhha.timer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class SampleTimer {

    private final MeterRegistry registry;
    private final Timer timer;
    private Timer.Sample sample;

    // Timer.Sample을 사용하면 작업의 시작과 종료 시점을 명시적으로 제어하여 측정할 수 있다
    public SampleTimer(MeterRegistry registry) {
        this.registry = registry;

        timer = Timer.builder("timer.sample")
                .description("time taken for a example task")
                .register(registry);
    }

    // sample의 시작 시간은 registry의 Clock을 기반으로 기록한다
    public void startTimer() {
        sample = Timer.start(registry);
    }

    public void doSomething() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopTimer() {
        sample.stop(timer);
    }
}
