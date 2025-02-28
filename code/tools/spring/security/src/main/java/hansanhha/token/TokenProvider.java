package hansanhha.token;

public interface TokenProvider<T, R> {

    R generateTokens(T t);

    R refreshToken(T t);
}
