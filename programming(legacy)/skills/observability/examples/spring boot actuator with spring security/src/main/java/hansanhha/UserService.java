package hansanhha;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    {
        for (int i =0; i<10; i++) {
            Long id = i + 1L;
            users.put(id, new User(id, "test user - " + id , (int) (id * 10)));
        }
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public List<User> getAll() {
        return users.values().stream().toList();
    }

    public void delete(Long id) {
        users.remove(id);
    }
}