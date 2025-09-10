package hansanhha.distribution_summary;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

public class ScalingBucketCardinality {

    private final DistributionSummary weightSummary;

    /*
        기본 버킷 범위: 1 ~ Long.MAX_VALUE
        scale 메서드를 통해 기본 버킷 범위를 조정할 수 있다
        serviceLevelObjectives 메서드로 slo 경계를 설정하여 그 경계에 도달하면 비율을 모니터링할 수 있다
     */
    public ScalingBucketCardinality(MeterRegistry registry) {
        weightSummary = DistributionSummary.builder("distribution.summary.scale")
                .scale(100)
                .baseUnit("kg")
                .serviceLevelObjectives(70, 80, 90)
                .register(registry);
    }

    public void recordWeight(int weight) {
        weightSummary.record(weight);
    }

}
