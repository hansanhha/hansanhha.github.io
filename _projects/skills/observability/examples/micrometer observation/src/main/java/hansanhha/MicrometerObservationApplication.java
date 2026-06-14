package hansanhha;

import hansanhha.observation.*;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.time.LocalDateTime;

public class MicrometerObservationApplication {

    public static void main(String[] args) {
        observation();
    }

    public static void observation() {
        UserService userService = new UserService();

        // 레지스트리 생성 (SimpleObservationRegistry)
        ObservationRegistry registry = ObservationRegistry.create();

        // 레지스트리 전역에 User 관련 ObservationHandler/Convention/Filter/Predicate 구현체 등록
        registry.observationConfig().observationHandler(new UserObservationHandler());
        registry.observationConfig().observationConvention(new UserObservationConvention());
        registry.observationConfig().observationFilter(new UserObservationFilter("dev"));
        registry.observationConfig().observationPredicate(new UserObservationPredicate());

        // User 엔티티 및 User 작업에 대한 관찰 정보(Observation, UserContext) 생성
        User user = new User("test user", "admin", LocalDateTime.now());
        Observation.Context context = new UserContext(user);

        Observation observation = Observation.createNotStarted("user.management", () -> context, registry);

        // observation name에 ignore가 포함되면 NoopObservation을 생성하는 UserObservationPredicate에 의해 계측 로직이 수행되지 않는다
//        Observation observation = Observation.createNotStarted("user.management.ignore", () -> context, registry);

        // 관찰할 동작(비즈니스 로직) 정의
        // 임의 이벤트 발행
        observation
                .observe(() -> {
                    // 이벤트 발행
                    observation.event(Observation.Event.of("task.before.event", "event before main task executing"));

                    // 비즈니스 로직: User 엔티티 저장
                    userService.create(user);
                });

        System.out.println("===========================");

        // 관찰할 동작(비즈니스 로직) 정의
        // 임의 예외 발행
        observation
                .observe(() -> {
                    // 비즈니스 로직: User 엔티티 조회
                    userService.getById(user.userId());

                    // 예외 발행
                    observation.error(new RuntimeException("unknown-error raised"));
                });
    }

}
