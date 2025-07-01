package hansanhha.client;

import hansanhha.token.jwt.JwtTokenProvider;
import hansanhha.token.TokenType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final static String defaultRedirectUrl = "/login/oauth2/success";
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT 발급
     * redirect
     * logging
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Map<String, String> tokenMap = jwtTokenProvider.generateTokens((OAuth2AuthenticationToken) authentication);

        URI uri = UriComponentsBuilder.fromUriString(defaultRedirectUrl)
                .queryParam(TokenType.ACCESS_TOKEN.getValue(), tokenMap.get(TokenType.ACCESS_TOKEN.getValue()))
                .queryParam(TokenType.REFRESH_TOKEN.getValue(), tokenMap.get(TokenType.REFRESH_TOKEN.getValue()))
                .build().toUri();

        log.info(this.getClass().getSimpleName());
        log.info("- redirect user-agent to the target URL");

        redirectStrategy.sendRedirect(request, response, uri.toString());
    }

}
