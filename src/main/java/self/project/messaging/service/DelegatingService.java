package self.project.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.messaging.dto.AccountDto;
import self.project.messaging.dto.AddUserToChatDto;
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

    public Long createChat(NewChatRqDto dto, String username) {
        var chatId = chatService.save(dto);

        var account = accountService.findByUsername(username);
        var addUserToChatDto = new AddUserToChatDto(account.getId(), chatId, account.getId());
        addUserToChat(addUserToChatDto);
        return chatId;
    }

    public MessageDto addUserToChat(AddUserToChatDto dto) {
        var account = accountService.findById(dto.userToAddId());
        var initiator = accountService.findById(dto.initiatorId());
        accountService.addUserToChat(dto.chatId(), dto.userToAddId());
        var messageDto = new MessageDto(
                            account.getUsername(),
                            new Sender(dto.initiatorId(), initiator.getUsername()),
                            MessageType.JOIN,
                            Instant.now(),
                            dto.chatId());
        messageService.save(messageDto);
        return messageDto;
    }
}
