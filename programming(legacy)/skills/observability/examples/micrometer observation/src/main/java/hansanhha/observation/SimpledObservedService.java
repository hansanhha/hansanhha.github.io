package hansanhha.observation;

import io.micrometer.observation.annotation.Observed;

public class SimpledObservedService {

    @Observed(name = "test.call", contextualName = "test#call",
            lowCardinalityKeyValues = { "abc", "123", "test", "42" })
    public void call() {
        System.out.println("call");
    }
}
