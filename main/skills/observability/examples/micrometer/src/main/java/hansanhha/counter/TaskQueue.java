package hansanhha.counter;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueue {

    private final AtomicInteger queueSize = new AtomicInteger(0);

    // FunctionCounter는 값을 누적하지 않고 현재 상태를 기반으로 동적으로 값을 계산한다
    public TaskQueue(MeterRegistry registry) {
        FunctionCounter.builder("counter.task.queue.size", queueSize, AtomicInteger::get)
                .description("current size of the task queue")
                .register(registry);
    }

    public void addTask() {
        queueSize.incrementAndGet();
    }

    public void completeTask() {
        queueSize.decrementAndGet();
    }

}
