package hansanhha.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    private String channel;
    private String channelId;
    private String boss;

    public ChannelTopic getChannelTopic() {
        return ChannelTopic.of(channel + "-" + channelId);
    }
}
