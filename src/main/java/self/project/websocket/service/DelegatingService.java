package self.project.websocket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import self.project.websocket.dto.ChatInfoDto;
import self.project.websocket.dto.MessageDto;
import self.project.websocket.mapper.DtoMessageMapper;
import self.project.websocket.model.Chat;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DelegatingService {

    private final ChatService chatService;
    private final MessageService messageService;

    public ChatInfoDto loadChatById(Long id) {
        Chat chat = chatService.findById(id);
        List<MessageDto> messageList = messageService
                .findByChat(id)
                .stream()
                .map(DtoMessageMapper.INSTANCE::toDto)
                .toList();
        return new ChatInfoDto(chat, messageList);
    }
}
