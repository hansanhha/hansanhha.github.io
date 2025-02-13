package hansanhha.client;

import hansanhha.client.vo.KaKaoOAuth2Attributes;
import hansanhha.client.vo.OAuth2Attributes;
import hansanhha.user.User;
import hansanhha.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.UUID;

@UserService
@RequiredArgsConstructor
@Slf4j
public class ClientOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2Attributes oauth2UserAttributes;
        OAuth2User oauth2User = super.loadUser(userRequest);

        log.info(this.getClass().getSimpleName());
        log.info("- bring the user information from the OAuth2 Resource Server");
        log.info("- user name : {}", oauth2User.getName());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("kakao")) {
            oauth2UserAttributes = KaKaoOAuth2Attributes.create(oauth2User.getAttributes());
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 인증입니다.");
        }

        Optional<User> findUser = userRepository.findByEmail(oauth2UserAttributes.getEmail());

        OAuth2User loginUser = convertOrCreateIfFirst(findUser, oauth2UserAttributes, userRequest);

        return loginUser;
    }

    private OAuth2User convertOrCreateIfFirst(Optional<User> findUser, OAuth2Attributes oAuth2Attributes, OAuth2UserRequest userRequest) {
        if (findUser.isPresent()) {
            return OAuth2LoginUser.from(findUser.get());
        }

        String nickname = UUID.randomUUID().toString().substring(0, 12);
        User user = User.createNormalUser(nickname, oAuth2Attributes.getEmail(), userRequest.getClientRegistration().getRegistrationId(), oAuth2Attributes.getUserNumber());
        userRepository.save(user);
        log.info("- create a new user");
        return OAuth2LoginUser.from(user);
    }
}
