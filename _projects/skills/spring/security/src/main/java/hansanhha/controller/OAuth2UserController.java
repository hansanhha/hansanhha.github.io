package hansanhha.controller;

import hansanhha.user.User;
import hansanhha.user.dto.UserInfoResponse;
import hansanhha.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserController {

    private final UserRepository userRepository;

    @GetMapping("/api/user")
    public UserInfoResponse getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String value  = user.getRole().getValue();
        String permission = "no";
        if (value.equals("ROLE_USER")) {
            permission = "user";
        }
        return new UserInfoResponse(user.getEmail(), user.getNickname(), permission);
    }
}
