package hansanhha.token.jwt;

import hansanhha.token.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Transactional
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider implements TokenProvider<OAuth2AuthenticationToken, Map<String, String>> {

    private static final String ISSUER = "hansanhha security service";
    private static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    private final JwtTokenService tokenService;

    @Value("${service.security.jwt.access-token-expiration}")
    private Instant accessTokenExpiredTime;

    @Value("${service.security.jwt.refresh-token-expiration}")
    private Instant refreshTokenExpiredTime;

    @Override
    public Map<String, String> generateTokens(OAuth2AuthenticationToken authentication) {
        String principalName = authentication.getName();
        Instant issueTime = Instant.now();

        String accessTokenId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();

        JwtGenerateSpec spec = JwtGenerateSpec.builder()
                .jwsHeader(header)
                .principalName(principalName)
                .issuer(ISSUER)
                .subject(principalName)
                .issueTime(issueTime)
                .accessTokenId(accessTokenId)
                .refreshTokenId(refreshTokenId)
                .accessTokenExpiredTime(issueTime.plus(accessTokenExpiredTime.toEpochMilli(), ChronoUnit.MILLIS))
                .refreshTokenExpiredTime(issueTime.plus(refreshTokenExpiredTime.toEpochMilli(), ChronoUnit.MILLIS))
                .build();

        var accessTokenAccessor = tokenService.generateAccessToken(spec);
        var refreshTokenAccessor = tokenService.generateRefreshToken(spec);

        log.info(this.getClass().getSimpleName());
        log.info("- generateTokens");

        return Map.of(accessTokenAccessor.getTokenType().getValue(), accessTokenAccessor.getAccessId(),
                refreshTokenAccessor.getTokenType().getValue(), refreshTokenAccessor.getAccessId());
    }

    @Override
    public Map<String, String> refreshToken(OAuth2AuthenticationToken authentication) {
        tokenService.revokeRefreshTokens(authentication.getName());
        return generateTokens(authentication);
    }

    @Getter
    public static class JwtGenerateSpec {
        private final JwsHeader jwsHeader;
        private final String principalName;
        private final String issuer;
        private final Instant issueTime;
        private final String subject;
        private final String accessTokenId;
        private final String refreshTokenId;
        private final Instant accessTokenExpiredTime;
        private final Instant refreshTokenExpiredTime;

        @Builder
        private JwtGenerateSpec(JwsHeader jwsHeader, String principalName, String issuer, Instant issueTime, String subject, String accessTokenId, String refreshTokenId, Instant accessTokenExpiredTime, Instant refreshTokenExpiredTime) {
            this.jwsHeader = jwsHeader;
            this.principalName = principalName;
            this.issuer = issuer;
            this.issueTime = issueTime;
            this.subject = subject;
            this.accessTokenId = accessTokenId;
            this.refreshTokenId = refreshTokenId;
            this.accessTokenExpiredTime = accessTokenExpiredTime;
            this.refreshTokenExpiredTime = refreshTokenExpiredTime;
        }
    }
}
