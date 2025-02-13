package hansanhha.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisPubSubTest {

    @Autowired
    private ChatService chatService;



    @Test
    @DisplayName("메시지 전송 및 수신")
    void chatMessageSendAndReceive() {
        String hansanhha = "hansanhha";
        String userX = "userX";

        ChatRoom chatRoom = chatService.createChatRoom("test channel", hansanhha);

        chatService.chat(chatRoom, hansanhha, "hello " + userX);
        chatService.chat(chatRoom, userX, "hello " + hansanhha);
    }
}
