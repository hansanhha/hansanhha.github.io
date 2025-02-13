package hansanhha.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {

    private String email;
    private String nickname;
    private String permission;

    public UserInfoResponse(String email, String nickname, String permission) {
        this.email = email;
        this.nickname = nickname;
        this.permission = permission;
    }
}
