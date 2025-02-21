package hansanhha.observation;

import io.micrometer.observation.Observation;

// User 엔티티에 대한 관찰 정보를 저장하는 Context 객체
public class UserContext extends Observation.Context {

    private final String userId;
    private final String role;

    public UserContext(User user) {
        this.userId = user.userId();
        this.role = user.role();
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

}

