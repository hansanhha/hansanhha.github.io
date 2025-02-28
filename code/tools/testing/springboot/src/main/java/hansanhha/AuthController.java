package hansanhha;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/auth")
    public String getAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "hello " + authentication;
    }
}
