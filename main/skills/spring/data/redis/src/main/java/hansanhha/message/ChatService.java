package hansanhha.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RedisMessageListenerContainer messageListenerContainer;
    private final ChatMessageSender messageSender;
    private final ChatMessageConverter messageConverter;

    public ChatRoom createChatRoom(String channel, String boss) {
        return new ChatRoom(channel, getRandomChannelId(), boss);
    }

    public ChatMessage chat(ChatRoom chatRoom, String sender, String message) {
        messageListenerContainer.addMessageListener(new ChatMessageListener(messageConverter), chatRoom.getChannelTopic());

        return messageSender.send(ChatMessage.write(chatRoom, sender, message));
    }

    private static String getRandomChannelId() {
        return UUID.randomUUID().toString().substring(0, 4);
    }
}
