package hansanhha;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private Map<Long, String> userRepository = new HashMap<>();

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId) {
        return userRepository.get(userId);
    }

    @PostMapping
    public ResponseEntity<String> saveUser(String name) {
        userRepository.put((long) userRepository.size(), name);
        return ResponseEntity.created(URI.create("localhost:8080/api/users/" + (userRepository.size() - 1))).body(name);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userRepository.remove(userId);
    }
}
