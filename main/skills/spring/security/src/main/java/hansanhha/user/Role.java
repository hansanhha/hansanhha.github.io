package hansanhha.user;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {

    USER("ROLE_USER",
            Set.of(Authority.READ, Authority.WRITE, Authority.DELETE)
    );

    private String value;

    private Set<Authority> authorities;

    Role(String value, Set<Authority> authorities) {
        this.value = value;
        this.authorities = authorities;
    }
}
