package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.AccountDto;
import self.project.messaging.dto.ChatFullDto;
import self.project.messaging.dto.ChatShortDto;
import self.project.messaging.dto.MessageDto;
import self.project.messaging.dto.MessageDto.Sender;
import self.project.messaging.dto.MessageType;
import self.project.messaging.dto.NewChatRqDto;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegatingService {

    private final ChatService chatService;
    private final MessageService messageService;
    private final AccountService accountService;

    public ChatFullDto loadChatById(Long id) {
        ChatShortDto chatDto = chatService.findByIdShort(id);
        List<AccountDto> participants = accountService.findByChatId(id);
        List<MessageDto> messageList = messageService.findByChatId(id);
        return new ChatFullDto(chatDto, participants, messageList);
    }

    public void addUserToChat(MessageDto messageDto) {
        AccountDto account = accountService.findById(Long.parseLong(messageDto.getContent()));
        if (account == null) {
            throw new IllegalArgumentException("User not found with id " + messageDto.getContent());
        }
        accountService.addUserToChat(messageDto.getChatId(), account.getId());
        messageDto.setContent(account.getUsername());
        messageService.save(messageDto);
    }

    public void createChat(NewChatRqDto dto, String username) {
        var chatId = chatService.save(dto);

        var account = accountService.findByUsername(username);
        var messageDto = new MessageDto(
                            account.getId().toString(),
                            new Sender(account.getId(), username),
                            MessageType.JOIN,
                            Instant.now(),
                            chatId);
        addUserToChat(messageDto);
    }
}
