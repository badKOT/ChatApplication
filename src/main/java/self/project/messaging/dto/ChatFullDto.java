package self.project.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatFullDto {

    private Long id;
    private String title;
    private List<AccountDto> participants;
    private List<MessageDto> messageList;

    public ChatFullDto(ChatIdTitleDto shortDto, List<AccountDto> participants, List<MessageDto> messageList) {
        this.id = shortDto.getId();
        this.title = shortDto.getTitle();
        this.participants = participants;
        this.messageList = messageList;
    }
}
