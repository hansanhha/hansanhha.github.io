package hansanhha.observation;

import java.util.HashMap;
import java.util.Map;

// User 엔티티를 관리하는 서비스 객체
public class UserService {

    private final Map<String, User> userRepository = new HashMap<>();

    public User create(User user) {
        userRepository.put(user.userId(), user);
        System.out.println("business[created user]: " + user);
        return user;
    }

    public User getById(String userId) {
        User user = userRepository.get(userId);
        System.out.println("business[found user]:" + user);
        return user;
    }

    public void delete(String userId) {
        User user = userRepository.get(userId);
        System.out.println("business[removed user]: " + user);
        userRepository.remove(userId);
    }
}
