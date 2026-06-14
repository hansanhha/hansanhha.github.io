package hansanhha.observation;

import io.micrometer.observation.ObservationTextPublisher;
import io.micrometer.observation.aop.ObservedAspect;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SimpledObservedServiceTest {

    @Test
    void observedAnnotationTest() {

        // 테스트 레지스트리 생성
        TestObservationRegistry registry = TestObservationRegistry.create();
        registry.observationConfig().observationHandler(new ObservationTextPublisher());

        // 프록시 생성 및 aspect 추가
        AspectJProxyFactory pf = new AspectJProxyFactory(new SimpledObservedService());
        pf.addAspect(new ObservedAspect(registry));

        // 비즈니스 로직 호출
        SimpledObservedService service = pf.getProxy();
        service.call();

        // Observation 생성 검증
        assertThat(registry)
                .hasSingleObservationThat()
                .hasBeenStopped()
                .hasNameEqualTo("test.call")
                .hasContextualNameEqualTo("test#call")
                .hasLowCardinalityKeyValue("abc", "123")
                .hasLowCardinalityKeyValue("test", "42")
                .hasLowCardinalityKeyValue("class", SimpledObservedService.class.getName())
                .hasLowCardinalityKeyValue("method", "call").doesNotHaveError();
    }
}