package hansanhha.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@RequiredArgsConstructor
public class ChatMessageListener implements MessageListener {

    private final ChatMessageConverter messageConverter;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ChatMessage chatMessage = messageConverter.convert(message);

        System.out.println("==== 메시지 수신 ====");
        System.out.println(chatMessage.content());
    }
}
