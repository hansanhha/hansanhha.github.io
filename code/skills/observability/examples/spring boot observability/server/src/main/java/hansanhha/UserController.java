package hansanhha;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    String getUsername(@PathVariable("userId") Long userId) {
        return userService.getUsername(userId);
    }

    @PostMapping
    String createUser() {
        userService.createUser();
        return "created user";
    }
}
