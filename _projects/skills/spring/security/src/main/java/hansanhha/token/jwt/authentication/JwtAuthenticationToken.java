package hansanhha.token.jwt.authentication;

import hansanhha.token.jwt.JwtLoginUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtLoginUser loginUser;

    public static JwtAuthenticationToken authenticated(JwtLoginUser loginUser) {
        Assert.notNull(loginUser, "loginUser cannot be null");
        return new JwtAuthenticationToken(loginUser);
    }

    private JwtAuthenticationToken(JwtLoginUser loginUser) {
        super(loginUser.getAuthorities());
        this.loginUser = loginUser;
        setAuthenticated(true);
        // setDetails();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return loginUser;
    }
}
