package hansanhha.gauge;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleFunction;

public class BasicGauge {

    private final MeterRegistry registry;

    public BasicGauge(MeterRegistry registry) {
        this.registry = registry;
    }

    // Number 타입에 대한 Gauge 생성
    public void numberGauge() {
        AtomicInteger state = new AtomicInteger(0);
        registry.gauge("gauge.number", state);

        state.decrementAndGet();
        state.decrementAndGet();
        state.decrementAndGet();
    }

    // Collection 타입에 대한 Gauge 생성
    public void listGauge() {
        List<String> state = new ArrayList<>();
        registry.gauge("gauge.list", Tags.empty(), state, List::size);

        state.add("hello");
        state.add("list gauge");
    }

    // Map 타입에 대한 Gauge 생성
    public void mapGauge() {
        HashMap<String, Integer> state = new HashMap<>();

        registry.gaugeMapSize("gauge.map", Tags.empty(), state);

        state.put("hello", 1);
        state.put("map gauge", 2);
    }

    // Gauge builder 이용
    public void gaugeBuilder() {
        ArrayList<Integer> state = new ArrayList<>();
        ToDoubleFunction<ArrayList<Integer>> callback = List::size;

        Gauge gauge = Gauge
                .builder("gauge.builder", state, callback)
                .description("gauge description")
                .tag("type", "test gauge")
                .register(registry);

        for (int i = 0; i < 10; i++) {
            state.add(1);
        }
    }

}
