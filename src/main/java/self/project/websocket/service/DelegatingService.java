package self.project.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.websocket.dto.ChatInfoDto;
import self.project.websocket.dto.MessageDto;
import self.project.websocket.mapper.DtoChatMapper;
import self.project.websocket.mapper.DtoMessageMapper;
import self.project.websocket.model.Account;
import self.project.websocket.model.Chat;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegatingService {

    private final ChatService chatService;
    private final MessageService messageService;
    private final AccountService accountService;

    public ChatInfoDto loadChatById(Long id) {
        Chat chat = chatService.findById(id);
        List<MessageDto> messageList = messageService
                .findByChat(id)
                .stream()
                .map(DtoMessageMapper.INSTANCE::toDto)
                .sorted(Comparator.comparing(MessageDto::getSent))
                .toList();
        System.out.println(messageList);
        return new ChatInfoDto(DtoChatMapper.INSTANCE.toDto(chat), messageList);
    }

    public void saveMessage(MessageDto messageDto) {
        Account account = accountService.findByUsername(messageDto.getSender());
        Chat chat = chatService.findById(messageDto.getChatId());

        messageService.save(messageDto, account, chat);
    }
}
