package hansanhha.distribution_summary;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

public class ResponseSizeTracker {

    private final DistributionSummary responseSizeSummary;

    /*
        빌더를 통해 값을 누적할 DistributionSummary 생성한다
        publishPercentiles 메서드로 백분위를 계산하도록 설정할 수 있다
     */
    public ResponseSizeTracker(MeterRegistry registry) {
        responseSizeSummary = DistributionSummary.builder("distribution.summary")
                .description("distribution of http response sizes")
                .baseUnit("bytes")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }

    public void recordResponseSize(long byteSize) {
        responseSizeSummary.record(byteSize);
    }
}
