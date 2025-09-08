package hansanhha.timer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public class BasicTimer {

    private final Timer timer;

    public BasicTimer(MeterRegistry registry) {
        timer = Timer.builder("timer.record")
                .description("time taken for a example task")
                .register(registry);
    }

    // timer.record(Runnable) 사용
    public void executeRunnableTask() {
        timer.record(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    // timer.record(Supplier<T>) 사용
    public String executeSupplierTask() {
        return timer.record(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return "completed";
        });
    }

}
