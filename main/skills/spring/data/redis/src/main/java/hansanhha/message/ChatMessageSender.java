package hansanhha.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageSender {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatMessage send(ChatMessage chatMessage) {
        redisTemplate.convertAndSend(chatMessage.getChatRoom().getChannelTopic().getTopic(), convert(chatMessage));

        System.out.println("==== 메시지 전송 ====");
        System.out.println(chatMessage.content());

        return chatMessage;
    }

    private String convert(ChatMessage chatMessage) {
        try {
            return objectMapper.writeValueAsString(chatMessage);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException();
        }
    }

}
