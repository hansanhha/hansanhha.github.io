package hansanhha.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageConverter implements Converter<Message, ChatMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public ChatMessage convert(Message message) {
        String deserialized = RedisMessageSerializer.getStringSerializer().deserialize(message.getBody());

        try {
            return objectMapper.readValue(deserialized, ChatMessage.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class RedisMessageSerializer {

        public static class MessageSerializers {
            private static final RedisSerializer<String> STRING_REDIS_SERIALIZER = new Jackson2JsonRedisSerializer<>(String.class);
        }

        public static RedisSerializer<String> getStringSerializer() {
            return MessageSerializers.STRING_REDIS_SERIALIZER;
        }

    }
}
