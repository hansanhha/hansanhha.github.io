package hansanhha.token;

import hansanhha.token.jwt.access.TokenAccessor;

public interface TokenService<Spec> {

    TokenAccessor<?> generateAccessToken(Spec spec);

    TokenAccessor<?> generateRefreshToken(Spec spec);

    TokenAccessor<?> resolveToken(String value);

    void removeToken(TokenAccessor<?> accessor);
}
