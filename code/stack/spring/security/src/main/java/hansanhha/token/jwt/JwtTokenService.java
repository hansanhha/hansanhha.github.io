package hansanhha.token.jwt;

import hansanhha.token.Token;
import hansanhha.token.TokenService;
import hansanhha.token.TokenType;
import hansanhha.token.jwt.access.JwtAccessTokenAccessor;
import hansanhha.token.jwt.access.JwtRefreshTokenAccessor;
import hansanhha.token.jwt.access.TokenAccessor;
import hansanhha.token.jwt.repository.JwtAccessTokenRepository;
import hansanhha.token.jwt.repository.JwtRefreshTokenRepository;
import hansanhha.user.User;
import hansanhha.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService<JwtTokenProvider.JwtGenerateSpec> {

    private final JwtAccessTokenRepository accessTokenRepository;
    private final JwtRefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Override
    public TokenAccessor<Jwt> generateAccessToken(JwtTokenProvider.JwtGenerateSpec spec) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(spec.getAccessTokenId())
                .issuer(spec.getIssuer())
                .issuedAt(spec.getIssueTime())
                .expiresAt(spec.getAccessTokenExpiredTime())
                .subject(spec.getSubject())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(spec.getJwsHeader(), claims);
        Jwt jwt = jwtEncoder.encode(parameters);

        User user = userRepository.findByEmail(spec.getPrincipalName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        saveAccessToken(jwt, user);

        return JwtAccessTokenAccessor.from(jwt);
    }

    @Override
    public TokenAccessor<Jwt> generateRefreshToken(JwtTokenProvider.JwtGenerateSpec spec) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(spec.getRefreshTokenId())
                .issuer(spec.getIssuer())
                .issuedAt(spec.getIssueTime())
                .expiresAt(spec.getRefreshTokenExpiredTime())
                .subject(spec.getSubject())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(spec.getJwsHeader(), claims);
        Jwt jwt = jwtEncoder.encode(parameters);

        User user = userRepository.findByEmail(spec.getPrincipalName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        saveRefreshToken(jwt, user);

        return JwtRefreshTokenAccessor.from(jwt);
    }

    /*
        * 토큰 검색, 유효성 검증, TokenAccessor 반환
        * @param value : 토큰 값
        * @return : 토큰에 대한 정보를 담은 TokenAccessor 객체
        * @throws IllegalArgumentException : 토큰이 존재하지 않을 경우
     */
    @Override
    public TokenAccessor<Jwt> resolveToken(String value) {
        Optional<Token> token;

        if ((token = findAccessToken(value)).isPresent()) {
            return convertTokenToAccessorWithValidate(TokenType.ACCESS_TOKEN, token.get());
        } else if ((token = findRefreshToken(value)).isPresent()) {
            return convertTokenToAccessorWithValidate(TokenType.REFRESH_TOKEN, token.get());
        } else {
            throw new IllegalArgumentException("Token not found");
        }
    }

    private TokenAccessor<Jwt> convertTokenToAccessorWithValidate(TokenType type, Token token) throws JwtException {
        Jwt decodedJwt = jwtDecoder.decode(token.getTokenValue());

        return switch (type) {
            case ACCESS_TOKEN -> JwtAccessTokenAccessor.from(decodedJwt);
            case REFRESH_TOKEN -> JwtRefreshTokenAccessor.from(decodedJwt);
        };
    }

    private Optional<Token> findRefreshToken(String value) {
        return refreshTokenRepository.findByTokenValue(value);
    }

    private Optional<Token> findAccessToken(String value) {
        return accessTokenRepository.findByTokenValue(value);
    }


    @Override
    public void removeToken(TokenAccessor<?> accessor) {
        TokenType type = accessor.getTokenType();

        switch (type) {
            case ACCESS_TOKEN -> {
                Token token = accessTokenRepository.findByAccessId(accessor.getAccessId()).orElseThrow(() -> new IllegalArgumentException("Token not found"));
                accessTokenRepository.delete(token);
            }
            case REFRESH_TOKEN -> {
                Token token = refreshTokenRepository.findByAccessId(accessor.getAccessId()).orElseThrow(() -> new IllegalArgumentException("Token not found"));
                refreshTokenRepository.delete(token);
            }
        }
    }

    public TokenAccessor<Jwt> loadTokenByAccessTokenValue(String value) {
        Token token = accessTokenRepository.findByTokenValue(value).orElseThrow(() -> new IllegalArgumentException("Token not found"));
        Jwt jwt = jwtDecoder.decode(token.getTokenValue());
        return JwtAccessTokenAccessor.from(jwt);
    }

    public TokenAccessor<Jwt>  loadTokenByAccessId(TokenType type, String accessId) {
        return switch (type) {
            case ACCESS_TOKEN -> {
                Token token = accessTokenRepository.findByAccessId(accessId).orElseThrow(() -> new IllegalArgumentException("Token not found"));
                Jwt jwt = jwtDecoder.decode(token.getTokenValue());
                yield JwtAccessTokenAccessor.from(jwt);
            }
            case REFRESH_TOKEN -> {
                Token token = refreshTokenRepository.findByAccessId(accessId).orElseThrow(() -> new IllegalArgumentException("Token not found"));
                Jwt jwt = jwtDecoder.decode(token.getTokenValue());
                yield JwtRefreshTokenAccessor.from(jwt);
            }
        };
    }

    public void revokeRefreshTokens(String userId) {
        User user = userRepository.findByEmail(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Token> tokens = refreshTokenRepository.findAllByUser(user);

        tokens.forEach(token -> token.setUsed(false));
    }

    private void saveAccessToken(Jwt jwt, User user) {
        Token token = Token.builder()
                .accessId(jwt.getClaim(JwtClaimNames.JTI))
                .tokenValue(jwt.getTokenValue())
                .issuedAt(jwt.getIssuedAt())
                .expiredAt(jwt.getExpiresAt())
                .user(user)
                .build();

        accessTokenRepository.save(token);
    }

    private void saveRefreshToken(Jwt jwt, User user) {
        Token token = Token.builder()
                .accessId(jwt.getClaim(JwtClaimNames.JTI))
                .tokenValue(jwt.getTokenValue())
                .issuedAt(jwt.getIssuedAt())
                .expiredAt(jwt.getExpiresAt())
                .user(user)
                .build();

        refreshTokenRepository.save(token);
    }
}
