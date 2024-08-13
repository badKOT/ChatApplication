package self.project.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import self.project.messaging.model.Message.MessageType;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageDto {

    private String content;
    private String sender;
    private MessageType type;
    private Instant sent;
    private Long chatId;
}
