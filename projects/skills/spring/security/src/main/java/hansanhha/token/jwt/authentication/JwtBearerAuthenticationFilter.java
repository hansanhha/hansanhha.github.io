package hansanhha.token.jwt.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import hansanhha.token.jwt.JwtLoginUser;
import hansanhha.token.jwt.JwtTokenService;
import hansanhha.user.User;
import hansanhha.user.repository.UserRepository;
import hansanhha.util.RequestMatcherRegistry;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtBearerAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtTokenService tokenService;
    private final RequestMatcherRegistry requestMatcherRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ERROR_EMPTY_TOKEN_MESSAGE = "Token is Empty";
    private static final String ERROR_VALIDATION_MESSAGE = "Invalid Token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> extractedValue = extractToken(request);

        if (extractedValue.isEmpty()) {
            sendErrorMsg(response, ERROR_EMPTY_TOKEN_MESSAGE);
            return;
        }

        try {
            // 토큰 검증 및 Authentication 생성, 저장
            var accessor = tokenService.resolveToken(extractedValue.get());

            Jwt jwt = accessor.get();
            String email = jwt.getSubject();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

            JwtLoginUser loginUser = JwtLoginUser.from(user);
            JwtAuthenticationToken token = JwtAuthenticationToken.authenticated(loginUser);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(token);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (JwtException var1) {
            sendErrorMsg(response, ERROR_VALIDATION_MESSAGE);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return requestMatcherRegistry.anyMatches(request);
    }

    private void sendErrorMsg(HttpServletResponse response, String errorMsg) throws IOException {
        Map<String, String> message = Map.of("reason", errorMsg);

        response.getWriter().write(objectMapper.writeValueAsString(message));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith(AccessTokenType.BEARER.getValue())) {
            return Optional.of(bearerToken.substring(AccessTokenType.BEARER.getValue().length() + 1));
        }

        return Optional.empty();
    }
}
