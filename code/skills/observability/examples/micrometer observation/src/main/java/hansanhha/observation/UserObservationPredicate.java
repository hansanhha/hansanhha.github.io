package hansanhha.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationPredicate;

/*
    Observation 생성 전 평가되어 true 반환 시 Observation을 생성하고,
    false 반환 시 아무 계측 로직을 수행하지 않는 NoopObservation을 생성하는 ObservationPredicate 구현체
 */
public class UserObservationPredicate implements ObservationPredicate {

    @Override
    public boolean test(String observationName, Observation.Context context) {
        return !observationName.contains("ignore");
    }
}
