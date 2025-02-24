package hansanhha;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.observation.annotation.Observed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    private final Counter getUserRequestCounter;
    private final Map<Long, User> userRepository = new HashMap<>();

    {
        for (int i = 0; i < 5; i++) {
            User user = new User((long) i, UUID.randomUUID().toString());
            userRepository.put((long) i, user);
        }
    }

    // 스프링 부트의 자동 구성 기능으로 인해 MeterRegistry가 스프링 빈으로 등록되기 때문에 주입받을 수 있다
    // micrometer의 Counter 생성 및 registry 등록
    public UserController(MeterRegistry meterRegistry) {
        getUserRequestCounter = meterRegistry.counter("user.search.all.counter", Tags.of("service", "user"));
    }

    // counter 메트릭 사용
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        getUserRequestCounter.increment();
        return ResponseEntity.ok(userRepository.values().stream().toList());
    }

    // @Observed 사용
    @GetMapping("/{userId}")
    @Observed(name = "user.search.specific", contextualName = "get user", lowCardinalityKeyValues = {"api", "/api/users/{userId}", "service", "user"})
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userRepository.get(userId);
        return ResponseEntity.ok(user);
    }

    public record User (
            Long id,
            String name) {
    }
}
