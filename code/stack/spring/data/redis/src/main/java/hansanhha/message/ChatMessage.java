package hansanhha.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private ChatRoom chatRoom;
    private String sender;
    private String payload;

    public String content() {
        return """
               - 채널: %s
               - 방장: %s
               - 송신자: %s
               - 내용: %s \n
               """.formatted(getChatRoom().getChannelTopic().getTopic(), getChatRoom().getBoss(), getSender(), getPayload());
    }

    public static ChatMessage write(ChatRoom chatRoom, String sender, String payload) {
        return new ChatMessage(chatRoom, sender, payload);
    }
}
