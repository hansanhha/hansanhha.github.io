package hansanhha.token;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }
}
