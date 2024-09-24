package self.project.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Message {
    private Long id;
    private String content;
    private Instant sent;
    private MessageType type;

    private Long senderId;
    private Long chatId;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
