package hansanhha.token.jwt.access;

import hansanhha.token.TokenType;

public interface TokenAccessor<T> {

    TokenType getTokenType();

    T get();

    default String getAccessId() {
        return null;
    }
}
