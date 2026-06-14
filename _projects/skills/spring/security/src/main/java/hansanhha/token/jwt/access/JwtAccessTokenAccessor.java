package hansanhha.token.jwt.access;

import hansanhha.token.TokenType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

public class JwtAccessTokenAccessor implements TokenAccessor<Jwt> {

    private final Jwt jwt;
    private final String accessId;
    private static final TokenType tokenType = TokenType.ACCESS_TOKEN;

    private JwtAccessTokenAccessor(Jwt jwt) {
        this.jwt = jwt;
        this.accessId = jwt.getClaim(JwtClaimNames.JTI);
    }

    public static JwtAccessTokenAccessor from(Jwt jwt) {
        return new JwtAccessTokenAccessor(jwt);
    }

    @Override
    public Jwt get() {
        return jwt;
    }

    @Override
    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String getAccessId() {
        return accessId;
    }
}
