package hansanhha.gauge;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpecialGauge {

    private final MeterRegistry registry;

    public SpecialGauge(MeterRegistry registry) {
        this.registry = registry;
    }

    public void timeGauge() {
        AtomicInteger msTimeGauge = new AtomicInteger(4000);

        // 특정 시간 단위(TimeUnit)을 지정하여 TimeGauge 생성
        TimeGauge timeGauge = TimeGauge
                .builder("gauge.time", msTimeGauge, TimeUnit.MILLISECONDS, AtomicInteger::get)
                .register(registry);
    }

    public void multiGauge() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        // MultiGauge 생성
        MultiGauge multiGauge = MultiGauge.builder("gauge.multi")
                .description("memory usage by category")
                .register(registry);

        /*
            MultiGauge에 개별 Gauge 등록
         */
        ArrayList<MultiGauge.Row<?>> rows = new ArrayList<>();

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        rows.add(MultiGauge.Row.of(Tags.of("type", "heap"), heapMemoryUsage.getUsed()));

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        rows.add(MultiGauge.Row.of(Tags.of("type", "non_heap"), nonHeapMemoryUsage.getUsed()));

        multiGauge.register(rows, true);
    }
}