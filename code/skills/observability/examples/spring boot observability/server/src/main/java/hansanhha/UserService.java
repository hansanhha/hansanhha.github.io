package hansanhha;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    @Observed(name = "user.name",
            contextualName = "getting-user-name",
            lowCardinalityKeyValues = {"userType", "userType2"})
    String getUsername(Long userId) {
        return "spring-man";
    }

    @Timed(value = "user.creation.timer", percentiles = {0.3, 0.5, 0.95}, description = "user creation process timer")
    public void createUser() {
        Random processTime = new Random();
        try {
            Thread.sleep(processTime.nextLong(100, 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
