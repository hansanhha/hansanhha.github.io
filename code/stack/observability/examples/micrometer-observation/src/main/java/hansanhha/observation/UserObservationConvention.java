package hansanhha.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.observation.GlobalObservationConvention;
import io.micrometer.observation.Observation;

// UserContext에 대한 기본 이름과 태그를 제공하는 ObservationConvention 구현체
// ObservationRegistry에 전역으로 등록하려면 GlobalObservationConvention 타입의 인터페이스를 구현해야 한다
public class UserObservationConvention implements GlobalObservationConvention<UserContext> {

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof UserContext;
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(UserContext context) {
        return KeyValues.of("user.role", context.getRole());
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(UserContext context) {
        return KeyValues.of("user.id", context.getUserId());
    }

    @Override
    public String getName() {
        return "user.info";
    }
}
