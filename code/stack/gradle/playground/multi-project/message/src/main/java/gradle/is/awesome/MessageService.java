package gradle.is.awesome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MessageService {

    public static String generateMessage() {
        List<String> messages = Arrays.asList("home alone", "inception");
        return messages.get(new Random().nextInt(messages.size()));
    }
}
