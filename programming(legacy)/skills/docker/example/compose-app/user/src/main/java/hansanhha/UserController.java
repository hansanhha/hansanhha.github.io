package hansanhha;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final Environment environment;
    private final List<User> users;

    public UserController(Environment environment, List<User> users) {
        this.environment = environment;
        this.users = users;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUser() {
        String[] activeProfiles = environment.getActiveProfiles();

        Optional<List<User>> users_ = Optional.ofNullable(users);

        return ResponseEntity.ok(
                Map.of("activated spring profiles by environment variables", activeProfiles,
                        "users", users_)
        );
    }
}
