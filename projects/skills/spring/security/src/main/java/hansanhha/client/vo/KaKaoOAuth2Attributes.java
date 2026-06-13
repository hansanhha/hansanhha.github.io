package hansanhha.client.vo;

import lombok.Getter;

import java.util.Map;

@Getter
public class KaKaoOAuth2Attributes implements OAuth2Attributes {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccountAttributes;

    @SuppressWarnings("unchecked")
    private KaKaoOAuth2Attributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
    }

    public static KaKaoOAuth2Attributes create(Map<String, Object> attributes) {
        return new KaKaoOAuth2Attributes(attributes);
    }

    @Override
    public String getUserNumber() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }
}
