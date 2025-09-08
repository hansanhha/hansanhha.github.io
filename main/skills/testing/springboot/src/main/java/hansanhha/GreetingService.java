package hansanhha;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String greet() {
        return "hello spring";
    }
}
