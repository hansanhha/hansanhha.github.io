package hansanhha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO
 *  1. SuccessHandler 구현 : JWT, redirect, Logging
 *      - Nimbus JOSE + JWT
 *  2. JWT Filter 구현
 *  3. OAuth2 Refresh Token 구현
 *  4. stateless : OAuth2AuthorizationRequest, OAuth2AuthorizationRequestRepository
 *  5. 2FA : google authenticator
 *  6. Error Handling : OAuth2Error, OAuth2AuthenticationException
 */
@SpringBootApplication
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }
}
