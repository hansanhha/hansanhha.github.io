package hansanhha.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;

// Observation 생성 후 Context 데이터를 동적으로 변경하는 ObservationFilter 구현체
public class UserObservationFilter implements ObservationFilter {

    private static final String envKey = "api.user.env";

    private final String environment;

    public UserObservationFilter(String environment) {
        this.environment = environment;
    }

    @Override
    public Observation.Context map(Observation.Context context) {
        if (context instanceof UserContext userContext && userContext.get(envKey) == null) {
            System.out.println("filter[add tag] key: " + envKey + ", value: " + environment);
            userContext.addLowCardinalityKeyValue(KeyValue.of(envKey, environment));
        }

        return context;
    }
}
